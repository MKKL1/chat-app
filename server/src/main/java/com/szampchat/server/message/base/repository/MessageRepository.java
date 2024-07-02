package com.szampchat.server.message.base.repository;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.message.base.MessageId;
import com.szampchat.server.message.base.entity.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface MessageRepository extends R2dbcRepository<Message, MessageId> {
//    @Query(value = "SELECT * FROM messages WHERE channel_id = :channelId ORDER BY updated_at DESC LIMIT :limit")
//    Flux<Message> findMessagesByChannelIdAndSortByLatest(@Param("channelId") Long channelId, @Param("limit") int limit);

    Flux<Message> findAllByChannel(Channel channel);
}
