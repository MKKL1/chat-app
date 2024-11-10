package com.szampchat.server.reaction;

import com.szampchat.server.message.entity.Message;
import com.szampchat.server.reaction.dto.ReactionCountDTO;
import com.szampchat.server.reaction.dto.ReactionListDTO;
import com.szampchat.server.reaction.entity.Reaction;
import com.szampchat.server.snowflake.SnowflakeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.mapping.event.BeforeConvertCallback;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import reactor.core.publisher.Mono;

@Configuration
public class ReactionConfiguration {
    private final SnowflakeCacheManager<Long> messageSnowflakeManager = new SnowflakeCacheManager<>();

    @Bean
    public ReactiveRedisTemplate<String, ReactionListDTO> reactionListDTOReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        Jackson2JsonRedisSerializer<ReactionListDTO> serializer = new Jackson2JsonRedisSerializer<>(ReactionListDTO.class);
        RedisSerializationContext<String, ReactionListDTO> context = RedisSerializationContext
                .<String, ReactionListDTO>newSerializationContext(RedisSerializer.string())
                .value(serializer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
