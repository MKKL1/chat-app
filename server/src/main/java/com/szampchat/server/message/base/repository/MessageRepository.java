package com.szampchat.server.message.base.repository;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.message.base.MessageId;
import com.szampchat.server.message.base.entity.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, MessageId> {
    @Query(value = "SELECT * FROM messages WHERE channel_id = :channelId ORDER BY updated_at DESC LIMIT :limit", nativeQuery = true)
    List<Message> findLatestMessagesForChannel(@Param("channelId") Long channelId, @Param("limit") int limit);

}
