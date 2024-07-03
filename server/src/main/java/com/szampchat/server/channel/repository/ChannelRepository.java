package com.szampchat.server.channel.repository;

import com.szampchat.server.channel.entity.Channel;
import jdk.jfr.Registered;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

@Registered
public interface ChannelRepository extends R2dbcRepository<Channel, Long> {
}
