package com.szampchat.server.message;

import com.szampchat.server.message.entity.MessageAttachment;
import com.szampchat.server.snowflake.SnowflakeGen;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import reactor.core.publisher.Mono;

@Configuration
@AllArgsConstructor
public class MessageAttachmentConfiguration {

    private final SnowflakeGen snowflakeGen;

    @Bean
    BeforeConvertCallback<MessageAttachment> beforeMessageAttachmentConvertCallback(){
        return ((entity, table) -> Mono.just(entity)
                .doOnNext(attachment -> {
                    if (attachment.getId() == null){
                        attachment.setId(snowflakeGen.nextId());
                    }
                }));
    }
}
