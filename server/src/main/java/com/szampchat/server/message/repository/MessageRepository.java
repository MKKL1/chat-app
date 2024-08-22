package com.szampchat.server.message.repository;

import com.szampchat.server.message.entity.Message;
import com.szampchat.server.message.entity.MessageId;
import org.springframework.data.domain.Limit;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, MessageId> {
    Flux<Message> findMessagesByChannelOrderByIdDesc(Long channelId, Limit limit);

    @Query("""
            SELECT * FROM messages
            WHERE channel_id = :channel AND message_id < :before
            ORDER BY message_id DESC
            LIMIT :limit""")
    Flux<Message> findMessagesByChannel(@Param("channel") Long channel,
                                        @Param("before") Long before,
                                        @Param("limit") int limit);
}
