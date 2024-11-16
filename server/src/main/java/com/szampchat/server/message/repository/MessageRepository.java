package com.szampchat.server.message.repository;

import com.szampchat.server.message.entity.Message;
import com.szampchat.server.message.entity.MessageId;
import org.springframework.data.domain.Limit;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Repository
public interface MessageRepository extends ReactiveCrudRepository<Message, MessageId> {
    Flux<Message> findMessagesByChannelOrderByIdDesc(Long channel, Limit limit);

    @Query("""
            SELECT * FROM messages
            WHERE channel_id = :channel AND message_id < :before
            ORDER BY message_id DESC
            LIMIT :limit""")
    Flux<Message> findMessagesByChannel(@Param("channel") Long channel,
                                        @Param("before") Long before,
                                        @Param("limit") int limit);

    Flux<Message> findMessagesByChannelAndIdIn(Long channel, Collection<Long> ids);

    //TODO for some reason deleteById doesn't work
    Mono<Void> deleteByChannelAndId(Long channel, Long id);

    Mono<Message> findMessageByChannelAndId(Long channel, Long id);
    @Query("""
        UPDATE messages
        SET text = :text, updated_at = NOW()
        WHERE channel_id = :channelId AND message_id = :id
""")
    Mono<Void> updateByChannelIdAndId(@Param("channelId") Long channelId,
                                          @Param("id") Long id,
                                          @Param("text") String text);
}
