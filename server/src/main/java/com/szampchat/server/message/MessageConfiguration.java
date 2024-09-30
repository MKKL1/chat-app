package com.szampchat.server.message;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.message.entity.Message;
import com.szampchat.server.snowflake.Snowflake;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import reactor.core.publisher.Mono;

@Configuration
@AllArgsConstructor
public class MessageConfiguration {
    private final SnowflakeCacheManager<Long> messageSnowflakeManager = new SnowflakeCacheManager<>();

    @Bean
    BeforeConvertCallback<Message> beforeMessageConvertCallback(){
        return ((entity, table) -> Mono.just(entity)
                .doOnNext(message -> {
                    if (message.getId() == null){
                        //Depending on channel, different snowflake generator should be used to minimize the chance of overflowing sequence counter
                        message.setId(messageSnowflakeManager.getNextId(message.getChannel()));
                    }
                }));
    }
}
