package com.szampchat.server.voice.service;

import com.szampchat.server.livekit.event.RoomFinishedEvent;
import com.szampchat.server.livekit.event.RoomStartedEvent;
import com.szampchat.server.voice.mapper.RoomMapper;
import com.szampchat.server.voice.repository.ParticipantRepository;
import com.szampchat.server.voice.repository.RoomRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Service
public class RoomEventService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final ParticipantRepository participantRepository;

    public Mono<Void> handleRoomStarted(RoomStartedEvent event) {
        String roomName = event.getRoom().getName();
        return roomRepository.addRoom(roomName, roomMapper.toDTO(event.getRoom()))
                .doOnSuccess(_ -> log.info("Room {} has been created.", roomName))
                .then();
    }

    public Mono<Void> handleRoomFinished(RoomFinishedEvent event) {
        String roomName = event.getRoom().getName();
        return roomRepository.removeRoom(roomName)
                .then(participantRepository.clearParticipantsForRoom(roomName))
                .doOnSuccess(_ -> log.info("Room {} and its participants have been cleared.", roomName));
    }
}
