package com.szampchat.server.message.base;

import com.szampchat.server.message.base.entity.Message;
import com.szampchat.server.message.base.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Flux<Message> getMessages(Long channelId, GetMessagesRequest getMessagesRequest) {
        return Mono.just(getMessagesRequest)
                .flatMapMany(request -> {
                    int limit = request.getLimit() != null ? request.getLimit() : 10;
                    if(request.getBefore() == null)
                        return findLatestMessages(channelId, limit);
                    return findMessagesBefore(channelId, request.getBefore(), limit);
                });
    }

    public Flux<Message> findLatestMessages(Long channelId, int limit) {
        return messageRepository.findMessagesByChannelOrderByIdDesc(channelId, Limit.of(limit));
    }

    public Flux<Message> findMessagesBefore(Long channel, Long before, int limit) {
        return messageRepository.findMessagesByChannel(channel, before, limit);
    }
}
