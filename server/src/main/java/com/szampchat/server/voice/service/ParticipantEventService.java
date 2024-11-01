package com.szampchat.server.voice.service;

import com.szampchat.server.livekit.dto.ParticipantDTO;
import com.szampchat.server.livekit.event.ParticipantJoinedEvent;
import com.szampchat.server.livekit.event.ParticipantLeftEvent;
import com.szampchat.server.voice.mapper.ParticipantMapper;
import com.szampchat.server.voice.repository.ParticipantRepository;
import com.szampchat.server.voice.repository.RoomRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class ParticipantEventService {
    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    public Mono<Void> handleParticipantJoined(ParticipantJoinedEvent event) {
        String roomName = event.getRoom().getName();
        ParticipantDTO participantDTO = participantMapper.toDTO(event.getParticipantInfo());
        return participantRepository.addParticipant(roomName, participantDTO)
                .doOnSuccess(_ -> log.info("Participant {} joined room {}", participantDTO.getIdentity(), roomName))
                .then();
    }

    public Mono<Void> handleParticipantLeft(ParticipantLeftEvent event) {
        String roomName = event.getRoom().getName();
        String participantIdentity = event.getParticipantInfo().getIdentity();
        return participantRepository.removeParticipant(roomName, participantIdentity)
                .doOnSuccess(_ -> log.info("Participant {} left room {}", participantIdentity, roomName))
                .then();
    }
}
