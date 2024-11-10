package com.szampchat.server.reaction;

import com.szampchat.server.reaction.entity.ReactionList;
import com.szampchat.server.snowflake.SnowflakeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class ReactionConfiguration {
    private final SnowflakeCacheManager<Long> messageSnowflakeManager = new SnowflakeCacheManager<>();

    @Bean
    public ReactiveRedisTemplate<String, ReactionList> reactionListDTOReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        Jackson2JsonRedisSerializer<ReactionList> serializer = new Jackson2JsonRedisSerializer<>(ReactionList.class);
        RedisSerializationContext<String, ReactionList> context = RedisSerializationContext
                .<String, ReactionList>newSerializationContext(RedisSerializer.string())
                .value(serializer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
