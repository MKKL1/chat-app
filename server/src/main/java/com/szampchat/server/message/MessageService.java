package com.szampchat.server.message;

import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.message.dto.*;
import com.szampchat.server.message.dto.request.FetchMessagesRequest;
import com.szampchat.server.message.dto.request.MessageCreateRequest;
import com.szampchat.server.message.entity.MessageAttachment;
import com.szampchat.server.message.entity.MessageId;
import com.szampchat.server.message.event.MessageCreateEvent;
import com.szampchat.server.message.exception.MessageNotFoundException;
import com.szampchat.server.message.repository.MessageAttachmentRepository;
import com.szampchat.server.message.entity.Message;
import com.szampchat.server.message.repository.MessageRepository;
import com.szampchat.server.reaction.ReactionService;
import com.szampchat.server.reaction.dto.ReactionOverviewBulkDTO;
import com.szampchat.server.reaction.dto.ReactionOverviewDTO;
import com.szampchat.server.reaction.dto.ReactionUsersBulkDTO;
import com.szampchat.server.snowflake.SnowflakeGen;
import com.szampchat.server.upload.FilePath;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final MessageAttachmentService messageAttachmentService;
    private final ReactionService reactionService;
    private final ModelMapper modelMapper;
    private final SnowflakeGen snowflakeGen;
    private final EventSink eventSender;
    private final FileStorageService fileStorageService;

    //Can't cache as it requires current user id
    //TODO rename MessageDTO to MessageFullDTO or something, as MessageDTO is not mapping Message entity object directly
    //TODO separate cacheable and non-cacheable parts of MessageFullDTO
    public Flux<MessageDTO> getMessages(Long channelId, FetchMessagesRequest fetchMessagesRequest, Long currentUserId) {
        if(fetchMessagesRequest.getMessages() != null && !fetchMessagesRequest.getMessages().isEmpty()){
            return getMessagesBulk(channelId, fetchMessagesRequest.getMessages(), currentUserId);
        }

        //TODO no need to wrapping it in Mono.just
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
                MessageDTO messageDTO = modelMapper.map(savedMessage, MessageDTO.class);

                // checking if file is attached and saving it
                if (file != null) {
                    return fileStorageService.save(file, FilePath.MESSAGE)
                        .flatMap(filePath -> {
                            MessageAttachment attachment = MessageAttachment.builder()
                                .message(savedMessage.getId())
                                .name(file.filename())
                                .channel(channelId)
                                .path(filePath)
                                .size(0)
                                .build();
                            return messageAttachmentRepository.save(attachment)
                                .flatMap(messageAttachment -> {
                                    messageDTO.setAttachments(List.of(modelMapper.map(messageAttachment, MessageAttachmentDTO.class)));
                                    return Mono.just(messageDTO);
                                });
                        });
                } else {
                    return Mono.just(messageDTO);
                }
            })
            // sending message to listening users
            // TODO send path to saved file
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
        return messageRepository.findById(new MessageId(messageId, channelId))
                .switchIfEmpty(Mono.error(new MessageNotFoundException(messageId)));
    }

    public Mono<Message> editMessage(Long messageId, Long channelId, String text, Long userId) {
        return getMessage(messageId, channelId)
            .flatMap(message -> {
//                if(!Objects.equals(message.getUser(), userId)){
//                    return Mono.error(new Exception("Message doesn't belong to user"));
//                }

                message.setText(text);
                return messageRepository.save(message);
            });
    }

    public Mono<Void> deleteMessage(Long messageId, Long channelId, Long userId){
        return getMessage(messageId, channelId)
            .flatMap(message -> {
                // TODO find attachment and delete it
                // deleting attached file


                //TODO check in pre authorize
//                if(!Objects.equals(message.getUser(), userId)){
//                    return Mono.error(new Exception("Message doesn't belong to user"));
//                }


                return messageRepository.deleteById(new MessageId(messageId, channelId));
            });
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
                .getReactionsForUser(message.getChannel(), message.getId(), message.getUser())
                .collectList();

        return Mono.zip(reactionsMono, attachmentsMono)
                .map(tuple -> {
                    List<ReactionOverviewDTO> reactionPreviews = tuple.getT1();
                    List<MessageAttachmentDTO> attachments = tuple.getT2();


                    MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
                    messageDTO.setAttachments(attachments);
                    messageDTO.setReactions(reactionPreviews);

                    return messageDTO;
                });
    }

    public Flux<MessageDTO> getMessagesBulk(Long channelId, Collection<Long> messageIds, Long userId) {
        return messageRepository.findMessagesByChannelAndIdIn(channelId, messageIds)
                .flatMap(message -> attachAdditionalDataToMessage(message, userId));
    }
}
