package com.szampchat.server.message.service;

import com.szampchat.server.channel.service.ChannelService;
import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.message.dto.*;
import com.szampchat.server.message.dto.request.FetchMessagesRequest;
import com.szampchat.server.message.dto.request.MessageCreateRequest;
import com.szampchat.server.message.dto.request.MessageEditRequest;
import com.szampchat.server.message.entity.MessageAttachment;
import com.szampchat.server.message.event.MessageCreateEvent;
import com.szampchat.server.message.event.MessageDeleteEvent;
import com.szampchat.server.message.event.MessageUpdateEvent;
import com.szampchat.server.message.exception.MessageNotFoundException;
import com.szampchat.server.message.repository.MessageAttachmentRepository;
import com.szampchat.server.message.entity.Message;
import com.szampchat.server.message.repository.MessageRepository;
import com.szampchat.server.reaction.service.ReactionService;
import com.szampchat.server.reaction.dto.ReactionOverviewDTO;
import com.szampchat.server.snowflake.SnowflakeGen;
import com.szampchat.server.upload.FilePathType;
import com.szampchat.server.upload.FileStorageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Limit;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Service("messageService")
@Slf4j
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final MessageAttachmentService messageAttachmentService;
    private final ReactionService reactionService;
    private final ChannelService channelService;
    private final ModelMapper modelMapper;
    private final SnowflakeGen snowflakeGen;
    private final EventSink eventSender;
    private final FileStorageService fileStorageService;

    public Flux<MessageDTO> getMessages(Long channelId, FetchMessagesRequest fetchMessagesRequest, Long currentUserId) {
        if(fetchMessagesRequest.getMessages() != null && !fetchMessagesRequest.getMessages().isEmpty()){
            return getMessagesBulk(channelId, fetchMessagesRequest.getMessages(), currentUserId);
        }

        return Mono.just(fetchMessagesRequest)
                .flatMapMany(request -> {
                    int limit = request.getLimit() != null ? request.getLimit() : 10;

                    if(request.getBefore() == null)
                        return findLatestMessages(channelId, limit);

                    return findMessagesBefore(channelId, request.getBefore(), limit);
                })
                .flatMap(message -> attachAdditionalDataToMessage(message, currentUserId));
    }

    public Mono<MessageDTO> createMessage(MessageCreateRequest createMessage, Long userId, Long channelId, FilePart file) {
        // generating snowflake for message
        Long messageId = snowflakeGen.nextId();

        // creating message object
        Mono<Message> createMessageMono = Mono.fromSupplier(() -> {
            Message message = modelMapper.map(createMessage, Message.class);
            message.setId(messageId);
            message.setUser(userId);
            message.setChannel(channelId);
            return message;
        });

        // saving message
        return createMessageMono.flatMap(messageRepository::save)
            .flatMap(savedMessage -> {
                MessageDTO messageDTO = toDTO(savedMessage);

                // checking if file is attached and saving it
                if (file != null) {
                    return fileStorageService.upload(file, FilePathType.MESSAGE)
                        .map(fileDTO -> MessageAttachment.builder()
                                .message(savedMessage.getId())
                                .name(file.filename())
                                .channel(channelId)
                                .path(fileDTO.getId().toString())//TODO should be renamed to imageId
                                .size(0)
                                .build()
                        ).flatMap(attachment -> messageAttachmentRepository.save(attachment) //TODO move to service
                                .map(messageAttachment -> {
                                    messageDTO.setAttachments(List.of(modelMapper.map(messageAttachment, MessageAttachmentDTO.class)));
                                    return messageDTO;
                                }));
                }
                return Mono.just(messageDTO);
            })
            // sending message to listening users
            .doOnSuccess(savedMessageDTO -> {
                eventSender.publish(MessageCreateEvent.builder()
                    .data(savedMessageDTO)
                    .recipient(Recipient.builder()
                        .context(Recipient.Context.COMMUNITY)
                        .id(createMessage.getCommunityId())
                        .build())
                    .build());
            });
    }

    public Mono<Message> getMessage(Long messageId, Long channelId) {
        return messageRepository.findMessageByChannelAndId(channelId, messageId)
                .switchIfEmpty(Mono.error(new MessageNotFoundException(messageId)));
    }

    public Mono<MessageDTO> editMessage(Long messageId, Long channelId, MessageEditRequest request) {
        return getMessage(messageId, channelId)
                .flatMap(message -> {
                    message.setText(request.text());
                    return messageRepository.updateByChannelIdAndId(channelId, messageId, request.text())
                            .thenReturn(message);
                }).flatMap(message -> {
                    MessageDTO messageDTO = toDTO(message);
                    return channelService.getChannel(channelId)
                            .map(channel -> MessageUpdateEvent.builder()
                                    .data(messageDTO)
                                    .recipient(Recipient.builder()
                                            .context(Recipient.Context.COMMUNITY)
                                            .id(channel.getCommunityId())
                                            .build())
                                    .build()
                            )
                            .doOnNext(eventSender::publish)
                            .thenReturn(messageDTO);
                });
    }

    public Mono<Void> deleteMessage(Long messageId, Long channelId) {
        return getMessage(messageId, channelId)
            .flatMap(message -> {
                // TODO find attachment and delete it
                // fileStorageService.delete()

                return messageRepository.deleteByChannelAndId(channelId, messageId);
            })
            .then(channelService.getChannel(channelId)
                .flatMap(channel -> {
                    MessageDeleteEvent event = MessageDeleteEvent.builder()
                        .data(messageId)
                        .recipient(Recipient.builder()
                            .context(Recipient.Context.COMMUNITY)
                            .id(channel.getCommunityId())
                            .build())
                        .build();
                    eventSender.publish(event);
                    return Mono.empty();
                })
            );
    }

    Flux<Message> findLatestMessages(Long channelId, int limit) {
        return messageRepository.findMessagesByChannelOrderByIdDesc(channelId, Limit.of(limit));
    }

    Flux<Message> findMessagesBefore(Long channel, Long before, int limit) {
        return messageRepository.findMessagesByChannel(channel, before, limit);
    }

    //Make it public, and allow for multiple messages to be processed
    // I'm not sure if it should stay like this, but works for now
    Mono<MessageDTO> attachAdditionalDataToMessage(Message message, Long currentUserId) {


        Mono<List<MessageAttachmentDTO>> attachmentsMono = messageAttachmentService
                .findMessageAttachments(message.getId(), message.getChannel())
                .collectList();

        Mono<List<ReactionOverviewDTO>> reactionsMono = reactionService
                .getReactionsForUser(message.getChannel(), message.getId(), currentUserId)
                .collectList();

        return Mono.zip(reactionsMono, attachmentsMono)
                .map(tuple -> {
                    List<ReactionOverviewDTO> reactionPreviews = tuple.getT1();
                    List<MessageAttachmentDTO> attachments = tuple.getT2();


                    MessageDTO messageDTO = toDTO(message);
                    messageDTO.setAttachments(attachments);
                    messageDTO.setReactions(reactionPreviews);

                    return messageDTO;
                });
    }

    /*
        public Flux<MessageDTO> getMessagesBulk(Long channelId, Collection<Long> messageIds, Long userId) {
        return messageRepository.findMessagesByChannelAndIdIn(channelId, messageIds)
                .collectList()
                .flatMapMany(messages -> reactionService.getReactionsForUserBulk(channelId, messageIds, userId)
                        .collectMap(
                                ReactionOverviewBulkDTO::getMessageId,
                                ReactionOverviewBulkDTO::getReactionOverviewDTOS
                        ).flatMapMany(map -> Flux.fromIterable(messages)
                                .map(message -> {
                                    MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
                                    messageDTO.setReactions(map.getOrDefault(message.getId(), List.of()).stream().toList());

                                    return messageDTO;
                                })
                        )
                );
     */

    public Flux<MessageDTO> getMessagesBulk(Long channelId, Collection<Long> messageIds, Long userId) {
        return messageRepository.findMessagesByChannelAndIdIn(channelId, messageIds)
                .flatMap(message -> attachAdditionalDataToMessage(message, userId));
    }

    private MessageDTO toDTO(Message message) {
        Long updatedAt = message.getUpdated_at() == null ? null : message.getUpdated_at().toEpochMilli();
        return MessageDTO.builder()
                .id(message.getId())
                .channel(message.getChannel())
                .text(message.getText())
                .updated_at(updatedAt)
                .user(message.getUser())
                .respondsToMessage(message.getRespondsToMessage())
                .gifLink(message.getGifLink())
                .build();
    }
}
