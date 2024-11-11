package com.szampchat.server.reaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.szampchat.server.reaction.entity.ReactionList;
import com.szampchat.server.shared.cache.JsonCodec;
import com.szampchat.server.snowflake.SnowflakeCacheManager;
import io.lettuce.core.AbstractRedisClient;
import io.lettuce.core.RedisClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.ByteBuffer;

@Configuration
public class ReactionConfiguration {

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
