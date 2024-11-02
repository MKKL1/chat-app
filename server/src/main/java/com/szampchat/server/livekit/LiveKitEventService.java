package com.szampchat.server.livekit;

import com.szampchat.server.livekit.event.*;
import io.livekit.server.WebhookReceiver;
import jakarta.annotation.PostConstruct;
import livekit.LivekitWebhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class LiveKitEventService {
    private final WebhookReceiver webhookReceiver;
    private final Map<String, Function<LivekitWebhook.WebhookEvent, LivekitEvent>> livekitEventMap = new HashMap<>();

    private final int bufferSize = 10;
    private final Scheduler scheduler = Schedulers.boundedElastic();

    //Reactor stuff
    private final Sinks.EmitFailureHandler emitFailureHandler = (_, emitResult) -> emitResult.equals(Sinks.EmitResult.FAIL_NON_SERIALIZED);
    private final Sinks.Many<LivekitEvent> events = Sinks.many().multicast().onBackpressureBuffer(bufferSize, false);

    public LiveKitEventService(WebhookReceiver webhookReceiver) {
        this.webhookReceiver = webhookReceiver;

        livekitEventMap.put("room_started", RoomStartedEvent::create);
        livekitEventMap.put("room_finished", RoomFinishedEvent::create);
        livekitEventMap.put("participant_joined", ParticipantJoinedEvent::create);
        livekitEventMap.put("participant_left", ParticipantLeftEvent::create);
        livekitEventMap.put("track_published", TrackPublishedEvent::create);
        livekitEventMap.put("track_unpublished", TrackUnpublishedEvent::create);
    }

    @PostConstruct
    private void start() {
        asFlux()
                .doOnNext(event -> log.debug("Event: {}", event.getEvent().toString()))
                .subscribe();
    }

    public void handleEventPayload(String payload) {
        //Auth handled by spring security
        LivekitWebhook.WebhookEvent event = webhookReceiver.receive(payload, null, true);
        events.emitNext(livekitEventMap.get(event.getEvent()).apply(event), emitFailureHandler);
    }

    /**
     * @return Flux of internal events
     */
    public Flux<LivekitEvent> asFlux() {
        return events.asFlux().publishOn(scheduler);
    }

    public <T> Flux<T> on(Class<T> clazz) {
        return asFlux()
                .ofType(clazz);
    }
}
