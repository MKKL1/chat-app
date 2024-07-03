package com.szampchat.server.message.base;

import com.szampchat.server.message.base.entity.Message;
import com.szampchat.server.message.base.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Flux<Message> findLatestMessagesForChannel(Long channelId, int limit) {
        return messageRepository.findMessagesByChannelOrderById(channelId, Limit.of(limit));
    }
}
