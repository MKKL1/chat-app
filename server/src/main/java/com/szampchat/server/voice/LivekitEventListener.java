package com.szampchat.server.voice;

import com.szampchat.server.voice.livekit.LiveKitEventService;
import com.szampchat.server.voice.livekit.dto.ParticipantDTO;
import com.szampchat.server.voice.livekit.event.ParticipantJoinedEvent;
import com.szampchat.server.voice.livekit.event.ParticipantLeftEvent;
import com.szampchat.server.voice.livekit.event.RoomFinishedEvent;
import com.szampchat.server.voice.livekit.event.RoomStartedEvent;
import com.szampchat.server.voice.repository.ParticipantRepository;
import com.szampchat.server.voice.repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Component
public class LivekitEventListener {
    private final LiveKitEventService liveKitEventService;
    private final RoomRepository roomRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantService participantService;
    private final RoomService roomService;

    @PostConstruct
    private void startLivekitListener() {
        Flux.merge(
                liveKitEventService.on(RoomStartedEvent.class).flatMap(this::handleRoomStartedEvent),
                liveKitEventService.on(RoomFinishedEvent.class).flatMap(this::handleRoomFinishedEvent),
                liveKitEventService.on(ParticipantJoinedEvent.class).flatMap(this::handleParticipantJoinedEvent),
                liveKitEventService.on(ParticipantLeftEvent.class).flatMap(this::handleParticipantLeftEvent)
        ).subscribe(
                null,
                error -> log.error("Error processing event: {}", error.getMessage())
        );
    }

    private Mono<Void> handleRoomStartedEvent(RoomStartedEvent event) {
        String roomName = event.getRoom().getName();
        return roomRepository.addRoom(roomName, roomService.toDTO(event.getRoom()))
                .doOnSuccess(_ -> log.info("Room {} has been created.", roomName))
                .then();
    }

    /**
     * Handles the RoomFinishedEvent by removing the room and clearing participants.
     */
    private Mono<Void> handleRoomFinishedEvent(RoomFinishedEvent event) {
        String roomName = event.getRoom().getName();
        return roomRepository.removeRoom(roomName)
                .then(participantRepository.clearParticipantsForRoom(roomName))
                .doOnSuccess(_ -> log.info("Room {} and its participants have been cleared.", roomName));
    }

    /**
     * Handles the ParticipantJoinedEvent by adding the participant to the room.
     */
    private Mono<Void> handleParticipantJoinedEvent(ParticipantJoinedEvent event) {
        String roomName = event.getRoom().getName();
        ParticipantDTO participantDTO = participantService.toDTO(event.getParticipantInfo());
        return participantRepository.addParticipant(roomName, participantDTO)
                .doOnSuccess(_ -> log.info("Participant {} joined room {}", participantDTO.getIdentity(), roomName));
    }

    /**
     * Handles the ParticipantLeftEvent by removing the participant from the room.
     */
    private Mono<Void> handleParticipantLeftEvent(ParticipantLeftEvent event) {
        String roomName = event.getRoom().getName();
        String participantIdentity = event.getParticipantInfo().getIdentity();
        return participantRepository.removeParticipant(roomName, participantIdentity)
                .doOnSuccess(_ -> log.info("Participant {} left room {}", participantIdentity, roomName));
    }
}
