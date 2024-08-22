package com.szampchat.server.channel.repository;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.community.entity.Community;
import jdk.jfr.Registered;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

@Registered
public interface ChannelRepository extends R2dbcRepository<Channel, Long> {
    Flux<Channel> findChannelsByCommunity(Long community);
}
