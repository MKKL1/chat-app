package com.szampchat.server.voice;

import com.szampchat.server.livekit.dto.ParticipantDTO;
import com.szampchat.server.livekit.dto.RoomDTO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class VoiceConfiguration {
    @Bean
    public ReactiveRedisTemplate<String, RoomDTO> redisRoomTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<RoomDTO> serializer = new Jackson2JsonRedisSerializer<>(RoomDTO.class);
        RedisSerializationContext<String, RoomDTO> context = RedisSerializationContext
                .<String, RoomDTO>newSerializationContext(RedisSerializer.string())
                .value(serializer)
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, ParticipantDTO> redisParticipantTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<ParticipantDTO> serializer = new Jackson2JsonRedisSerializer<>(ParticipantDTO.class);
        RedisSerializationContext<String, ParticipantDTO> context = RedisSerializationContext
                .<String, ParticipantDTO>newSerializationContext(RedisSerializer.string())
                .value(serializer)
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
