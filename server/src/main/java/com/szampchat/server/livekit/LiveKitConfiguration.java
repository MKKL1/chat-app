package com.szampchat.server.livekit;

import com.szampchat.server.livekit.auth.LiveKitAuthManager;
import com.szampchat.server.livekit.dto.ParticipantDTO;
import com.szampchat.server.livekit.dto.RoomDTO;
import io.livekit.server.RoomServiceClient;
import io.livekit.server.WebhookReceiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.authentication.ReactiveAuthenticationManager;

@Configuration
public class LiveKitConfiguration {

    @Bean
    public RoomServiceClient roomServiceClient(LiveKitProperties properties) {
        return RoomServiceClient.createClient(
                properties.getUrl(),
                properties.getKey(),
                properties.getSecret()
        );
    }

    @Bean
    public WebhookReceiver webhookReceiver(LiveKitProperties properties) {
        return new WebhookReceiver(properties.getKey(), properties.getSecret());
    }

    @Bean
    public LiveKitAuthManager liveKitAuthManager(LiveKitProperties properties) {
        return new LiveKitAuthManager(properties.getKey(), properties.getSecret());
    }
}
