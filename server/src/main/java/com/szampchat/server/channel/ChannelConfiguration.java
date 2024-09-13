package com.szampchat.server.channel;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.snowflake.Snowflake;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import reactor.core.publisher.Mono;

@Configuration
@AllArgsConstructor
public class ChannelConfiguration {
    private Snowflake snowflake;

    @Bean
    BeforeConvertCallback<Channel> beforeChannelConvertCallback(){
        return ((entity, table) -> Mono.just(entity)
                .doOnNext(community -> {
                    if (community.getId() == null){
                        community.setId(snowflake.nextId());
                    }
                }));
    }
}