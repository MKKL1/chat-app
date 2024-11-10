package com.szampchat.server.reaction;

import com.szampchat.server.reaction.dto.ReactionListDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class ReactionConfiguration {
//    @Bean
//    public ReactiveRedisTemplate<String, ReactionListDTO> reactionDTOReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
//        Jackson2JsonRedisSerializer<ReactionListDTO> serializer = new Jackson2JsonRedisSerializer<>(ReactionListDTO.class);
//        RedisSerializationContext<String, ReactionListDTO> context = RedisSerializationContext
//                .<String, ReactionListDTO>newSerializationContext(RedisSerializer.string())
//                .value(serializer)
//                .build();
//        return new ReactiveRedisTemplate<>(connectionFactory, context);
//    }
}
