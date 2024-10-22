package com.szampchat.server.user;

import com.szampchat.server.snowflake.SnowflakeGen;
import com.szampchat.server.user.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import reactor.core.publisher.Mono;

@Configuration
@AllArgsConstructor
public class UserConfiguration {
    private final SnowflakeGen snowflakeGen;

    @Bean
    BeforeConvertCallback<User> beforeConvertCallback() {
        return (entity, table) -> Mono.just(entity)
                .doOnNext(user -> {
                    if (user.getId() == null)
                        user.setId(snowflakeGen.nextId());
                });
    }
}
