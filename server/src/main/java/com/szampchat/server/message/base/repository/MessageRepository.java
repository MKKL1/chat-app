package com.szampchat.server.message.base.repository;

import com.szampchat.server.message.base.MessageId;
import com.szampchat.server.message.base.entity.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<Message, MessageId> {
}
