package com.szampchat.server.message;

import com.szampchat.server.message.dto.FetchMessagesDTO;
import com.szampchat.server.message.dto.MessageDTO;
import com.szampchat.server.message.repository.MessageAttachmentRepository;
import com.szampchat.server.message.entity.Message;
import com.szampchat.server.message.repository.MessageRepository;
import com.szampchat.server.message.repository.ReactionRepository;
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

    public Flux<MessageDTO> getMessages(Long channelId, FetchMessagesDTO fetchMessagesDTO) {
        return Mono.just(fetchMessagesDTO)
                .flatMapMany(request -> {
                    int limit = request.getLimit() != null ? request.getLimit() : 10;
                    if(request.getBefore() == null)
                        return findLatestMessages(channelId, limit);
                    return findMessagesBefore(channelId, request.getBefore(), limit);
                })
                .flatMap(message -> attachAdditionalDataToMessage(message, 123L)); //TODO change to actual user's ID
    }

    private Flux<Message> findLatestMessages(Long channelId, int limit) {
        return messageRepository.findMessagesByChannelOrderByIdDesc(channelId, Limit.of(limit));
    }

    private Flux<Message> findMessagesBefore(Long channel, Long before, int limit) {
        return messageRepository.findMessagesByChannel(channel, before, limit);
    }

    private Mono<MessageDTO> attachAdditionalDataToMessage(Message message, Long currentUserId) {
        return reactionRepository.fetchGroupedReactions(message.getChannel(), message.getId(), currentUserId)
                .collectList()
                .map(reactionPreviews -> {
                            MessageDTO messageDTO = modelMapper.map(reactionPreviews, MessageDTO.class);
                            messageDTO.setAttachments(List.of());//TODO there are no attachments in test data, so skipping this for now
                            messageDTO.setReactions(reactionPreviews);

                            return messageDTO;
                        });
    }
}
