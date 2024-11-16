package com.szampchat.server.upload;

import com.szampchat.server.upload.entity.FileEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Configuration
public class FileConfiguration {
    @Bean
    BeforeConvertCallback<FileEntity> beforeFileConvertCallback(){
        return ((entity, table) -> Mono.just(entity)
                .doOnNext(file -> {
                    if (file.getId() == null){
                        file.setId(UUID.randomUUID());
                    }
                }));
    }
}
