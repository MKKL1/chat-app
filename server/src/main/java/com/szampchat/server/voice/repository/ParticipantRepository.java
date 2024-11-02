package com.szampchat.server.voice.repository;

import com.szampchat.server.livekit.dto.ParticipantDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ParticipantRepository {
    Mono<Void> addParticipant(String roomId, ParticipantDTO participant);
    Flux<ParticipantDTO> findParticipantsByRoomId(String roomId);
    Mono<Void> removeParticipant(String roomId, String participantIdentity);
    Mono<Void> clearParticipantsForRoom(String roomId);
}
