package com.szampchat.server.voice;

import com.szampchat.server.livekit.LiveKitEventService;
import com.szampchat.server.livekit.event.ParticipantJoinedEvent;
import com.szampchat.server.livekit.event.ParticipantLeftEvent;
import com.szampchat.server.livekit.event.RoomFinishedEvent;
import com.szampchat.server.livekit.event.RoomStartedEvent;
import com.szampchat.server.voice.service.ParticipantEventService;
import com.szampchat.server.voice.service.RoomEventService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@AllArgsConstructor
@Component
public class LivekitEventListener {
    private final LiveKitEventService liveKitEventService;
    private final RoomEventService roomEventService;
    private final ParticipantEventService participantEventService;

    @EventListener(ApplicationReadyEvent.class)
    private void startLivekitListener() {
        Flux.merge(
                liveKitEventService.on(RoomStartedEvent.class).flatMap(roomEventService::handleRoomStarted),
                liveKitEventService.on(RoomFinishedEvent.class).flatMap(roomEventService::handleRoomFinished),
                liveKitEventService.on(ParticipantJoinedEvent.class).flatMap(participantEventService::handleParticipantJoined),
                liveKitEventService.on(ParticipantLeftEvent.class).flatMap(participantEventService::handleParticipantLeft)
        ).subscribe(
                null,
                error -> log.error("Error processing event: {}", error.getMessage())
        );
    }
}
