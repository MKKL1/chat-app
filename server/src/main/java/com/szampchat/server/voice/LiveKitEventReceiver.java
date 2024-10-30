package com.szampchat.server.voice;

import io.livekit.server.WebhookReceiver;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class LiveKitEventReceiver {

    @PostConstruct
    public void start() {
        WebhookReceiver webhookReceiver = new WebhookReceiver("apiKey", "secret");


    }
}
