package com.szampchat.server.message;

import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.message.dto.EditMessageDTO;
import com.szampchat.server.message.dto.FetchMessagesDTO;
import com.szampchat.server.message.dto.MessageCreateDTO;
import com.szampchat.server.message.dto.MessageDTO;
import com.szampchat.server.message.event.MessageCreateEvent;
import com.szampchat.server.message.repository.MessageAttachmentRepository;
import com.szampchat.server.message.entity.Message;
import com.szampchat.server.message.repository.MessageRepository;
import com.szampchat.server.message.repository.ReactionRepository;
import com.szampchat.server.snowflake.Snowflake;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Service
@Slf4j
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final ReactionRepository reactionRepository;
    private final ModelMapper modelMapper;
    private final Snowflake snowflake;
    private final EventSink eventSender;

    //Can't cache as it requires current user id
    //TODO rename MessageDTO to MessageFullDTO or something, as MessageDTO is not mapping Message entity object directly
    //TODO separate cacheable and non-cacheable parts of MessageFullDTO
    public Flux<MessageDTO> getMessages(Long channelId, FetchMessagesDTO fetchMessagesDTO, Long currentUserId) {
        return Mono.just(fetchMessagesDTO)
                .flatMapMany(request -> {
                    int limit = request.getLimit() != null ? request.getLimit() : 10;

                    if(request.getBefore() == null)
                        return findLatestMessages(channelId, limit);

                    return findMessagesBefore(channelId, request.getBefore(), limit);
                })
                .flatMap(message -> attachAdditionalDataToMessage(message, currentUserId));
    }

    public Mono<Message> createMessage(MessageCreateDTO createMessage, Long userId, Long channelId){
        Message message = modelMapper.map(createMessage, Message.class);
        message.setId(snowflake.nextId());
        message.setUser(userId);
        message.setChannel(channelId);
        MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);

        // publishing event
        eventSender.publish(MessageCreateEvent.builder()
                .data(messageDTO)
                .recipient(Recipient.builder()
                        .context(Recipient.Context.COMMUNITY)
                        .id(createMessage.getCommunityId())
                        .build())
                .build());

        // saving in db
        return messageRepository.save(message);
    }

    public Mono<Message> editMessage(String text, Long messageId, Long userId){
        return messageRepository.findById(messageId)
            .switchIfEmpty(Mono.error(new Exception("Message doesn't exist")))
            .flatMap(message -> {
                if(!Objects.equals(message.getUser(), userId)){
                    return Mono.error(new Exception("Message doesn't belong to user"));
                }

                message.setText(text);
                return messageRepository.save(message);
            });
    }

    public Mono<Void> deleteMessage(Long id, Long userId){
        return messageRepository.findById(id)
            .switchIfEmpty(Mono.error(new Exception("Message doesn't exist")))
            .flatMap(message -> {

                if(!Objects.equals(message.getUser(), userId)){
                    return Mono.error(new Exception("Message doesn't belong to user"));
                }

                return messageRepository.deleteById(message.getId());
            });
    }

    Flux<Message> findLatestMessages(Long channelId, int limit) {
        return messageRepository.findMessagesByChannelOrderByIdDesc(channelId, Limit.of(limit));
    }

    Flux<Message> findMessagesBefore(Long channel, Long before, int limit) {
        return messageRepository.findMessagesByChannel(channel, before, limit);
    }

    //Make it public, and allow for multiple messages to be processed
    Mono<MessageDTO> attachAdditionalDataToMessage(Message message, Long currentUserId) {
        return reactionRepository.fetchGroupedReactions(message.getChannel(), message.getId(), currentUserId)
                .collectList()
                .map(reactionPreviews -> {
                            MessageDTO messageDTO = modelMapper.map(message, MessageDTO.class);
                            messageDTO.setAttachments(List.of());//TODO there are no attachments in test data, so skipping this for now
                            messageDTO.setReactions(reactionPreviews);

                            return messageDTO;
                        });
    }
}
