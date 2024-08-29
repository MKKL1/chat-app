package com.szampchat.server.community;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.snowflake.Snowflake;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import reactor.core.publisher.Mono;

@Configuration
@AllArgsConstructor
public class CommunityConfiguration {
    private final Snowflake snowflake;

    @Bean
    BeforeConvertCallback<Community> beforeCommunityConvertCallback(){
        return ((entity, table) -> Mono.just(entity)
            .doOnNext(community -> {
                if (community.getId() == null){
                    community.setId(snowflake.nextId());
                }
            }));
    }
}
