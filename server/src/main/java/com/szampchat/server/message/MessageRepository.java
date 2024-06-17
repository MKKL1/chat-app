package com.szampchat.server.message;

import com.szampchat.server.channel.Channel;
import jdk.jfr.Registered;
import org.springframework.data.repository.CrudRepository;

@Registered
public interface MessageRepository extends CrudRepository<Message, Long> {
}
