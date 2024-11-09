package com.szampchat.server.reaction;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class ReactionConfiguration {
//    @Bean
//    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
//            ReactiveRedisConnectionFactory connectionFactory) {
//        RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
//                .<String, Object>newSerializationContext(RedisSerializer.string())
//                .value(RedisSerializer.json())
//                .build();
//        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
//    }
}
