package com.szampchat.server.voice.livekit;

import io.livekit.server.RoomServiceClient;
import io.livekit.server.WebhookReceiver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LiveKitConfiguration {

    @Bean
    public RoomServiceClient roomServiceClient() {
        return RoomServiceClient.createClient(
                "http://localhost:7880",
                "devkey",
                "secret");
    }

    @Bean
    public WebhookReceiver webhookReceiver() {
        return new WebhookReceiver("devkey", "secret");
    }
}
