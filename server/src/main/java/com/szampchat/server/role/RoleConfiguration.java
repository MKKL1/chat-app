package com.szampchat.server.role;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.snowflake.Snowflake;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Configuration
public class RoleConfiguration {
    private final Snowflake snowflake;

    @Bean
    BeforeConvertCallback<Role> beforeRoleConvertCallback(){
        return ((entity, table) -> Mono.just(entity)
                .doOnNext(role -> {
                    if (role.getId() == null){
                        role.setId(snowflake.nextId());
                    }
                }));
    }
}
