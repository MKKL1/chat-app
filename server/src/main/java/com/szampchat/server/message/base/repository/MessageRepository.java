package com.szampchat.server.message.base.repository;

import com.szampchat.server.message.base.entity.Message;
import com.szampchat.server.message.base.entity.MessageId;
import org.springframework.data.domain.Limit;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MessageRepository extends R2dbcRepository<Message, MessageId> {
    Flux<Message> findMessagesByChannelOrderById(Long channelId);
    Flux<Message> findMessagesByChannelOrderById(Long channelId, Limit limit);
}
