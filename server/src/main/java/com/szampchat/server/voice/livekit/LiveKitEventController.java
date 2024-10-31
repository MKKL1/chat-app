package com.szampchat.server.voice.livekit;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@RestController
public class LiveKitEventController {
    private final LiveKitEventService liveKitEventService;

    @PostMapping(value = "/livekit/webhook", consumes = "application/webhook+json")
    public Mono<Void> handleWebhook(@RequestBody String payload) {
        return Mono.fromRunnable(() -> {
            liveKitEventService.handleEventPayload(payload);
        });
    }
}
