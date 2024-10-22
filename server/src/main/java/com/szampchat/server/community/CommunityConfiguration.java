package com.szampchat.server.community;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.Invitation;
import com.szampchat.server.snowflake.SnowflakeGen;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import reactor.core.publisher.Mono;

@Configuration
@AllArgsConstructor
public class CommunityConfiguration {
    private final SnowflakeGen snowflakeGen;
    //TODO separated snowflake generators for different entities

    @Bean
    BeforeConvertCallback<Community> beforeCommunityConvertCallback(){
        return ((entity, table) -> Mono.just(entity)
            .doOnNext(community -> {
                if (community.getId() == null){
                    community.setId(snowflakeGen.nextId());
                }
            }));
    }

    // TODO UUID instead of snowflake
    @Bean
    BeforeConvertCallback<Invitation> beforeInvitationConvertCallback(){
        return ((entity, table) -> Mono.just(entity)
                .doOnNext(community -> {
                    if (community.getId() == null){
                        community.setId(snowflakeGen.nextId());
                    }
                }));
    }
}
