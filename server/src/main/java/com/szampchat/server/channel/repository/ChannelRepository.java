package com.szampchat.server.channel.repository;

import com.szampchat.server.channel.entity.Channel;
import jdk.jfr.Registered;
import org.springframework.data.repository.CrudRepository;

@Registered
public interface ChannelRepository extends CrudRepository<Channel, Long> {
}
