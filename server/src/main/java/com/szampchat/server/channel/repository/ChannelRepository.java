package com.szampchat.server.channel.repository;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.community.entity.Community;
import jdk.jfr.Registered;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Registered
public interface ChannelRepository extends R2dbcRepository<Channel, Long> {
    Flux<Channel> findChannelsByCommunityId(Long community);

    Mono<Channel> findChannelByName(String name);

    @Query("SELECT EXISTS (SELECT 1 FROM community_members WHERE community_id = :community AND user_id = :user)")
    Mono<Boolean> isMemberOfCommunity(@Param("community") Long community, @Param("user") Long user);

    @Query("SELECT EXISTS(SELECT 1 FROM channels WHERE name = :name)")
    Mono<Boolean> doesChannelExist(@Param("name") String name);
}
