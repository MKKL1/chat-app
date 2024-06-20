package com.szampchat.server.message.base;

import jdk.jfr.Registered;
import org.springframework.data.repository.CrudRepository;

@Registered
public interface MessageRepository extends CrudRepository<Message, MessageId> {
}
