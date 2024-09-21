package com.szampchat.server.message;

import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.Recipient;
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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final ReactionRepository reactionRepository;
    private final ModelMapper modelMapper;
    private final Snowflake snowflake;
    private final EventSink eventSender;

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

    Mono<MessageDTO> createMessage(MessageCreateDTO creatMessage, Long userId){
        MessageDTO message = MessageDTO.builder()
            .id(snowflake.nextId())
            .text(creatMessage.getText())
            .userId(userId)
            .channelId(creatMessage.getChannelId())
            .build();

        // publishing event
        eventSender.publish(MessageCreateEvent.builder()
                .data(message)
                .recipient(Recipient.builder()
                        .context(Recipient.Context.COMMUNITY)
                        .id(creatMessage.getCommunityId())
                        .build())
                .build());

        // TODO save in db
        return Mono.just(message);
    }

    Flux<Message> findLatestMessages(Long channelId, int limit) {
        return messageRepository.findMessagesByChannelOrderByIdDesc(channelId, Limit.of(limit));
    }

    Flux<Message> findMessagesBefore(Long channel, Long before, int limit) {
        return messageRepository.findMessagesByChannel(channel, before, limit);
    }

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
