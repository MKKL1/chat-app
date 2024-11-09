package com.szampchat.server.reaction;

import com.szampchat.server.livekit.dto.RoomDTO;
import com.szampchat.server.reaction.dto.ReactionDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class ReactionConfiguration {
    @Bean
    public ReactiveRedisTemplate<String, ReactionDTO> reactionDTOReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        Jackson2JsonRedisSerializer<ReactionDTO> serializer = new Jackson2JsonRedisSerializer<>(ReactionDTO.class);
        RedisSerializationContext<String, ReactionDTO> context = RedisSerializationContext
                .<String, ReactionDTO>newSerializationContext(RedisSerializer.string())
                .value(serializer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
