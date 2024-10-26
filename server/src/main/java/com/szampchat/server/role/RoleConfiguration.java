package com.szampchat.server.role;

import com.szampchat.server.role.entity.Role;
import com.szampchat.server.snowflake.SnowflakeGen;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Configuration
public class RoleConfiguration {
    private final SnowflakeGen snowflakeGen;

    @Bean
    BeforeConvertCallback<Role> beforeRoleConvertCallback(){
        return ((entity, table) -> Mono.just(entity)
                .doOnNext(role -> {
                    if (role.getId() == null){
                        role.setId(snowflakeGen.nextId());
                    }
                }));
    }
}
