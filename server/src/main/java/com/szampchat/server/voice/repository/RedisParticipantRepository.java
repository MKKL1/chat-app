package com.szampchat.server.voice.repository;

import com.szampchat.server.livekit.dto.ParticipantDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class RedisParticipantRepository implements ParticipantRepository{
    private final ReactiveRedisTemplate<String, ParticipantDTO> participantTemplate;
    private final ReactiveValueOperations<String, ParticipantDTO> participantOps;
    private static final String ROOM_CACHE_PREFIX = "room:";
    private static final String PARTICIPANT_CACHE_SUFFIX = ":participant:";

    public RedisParticipantRepository(ReactiveRedisTemplate<String, ParticipantDTO> participantTemplate) {
        this.participantTemplate = participantTemplate;
        participantOps = participantTemplate.opsForValue();
    }

    /**
     * Adds a participant to a room's list in Redis.
     */
    public Mono<Void> addParticipant(String roomId, ParticipantDTO participant) {
        String key = ROOM_CACHE_PREFIX + roomId + PARTICIPANT_CACHE_SUFFIX + participant.getIdentity();
        return participantOps.set(key, participant)
                .doOnSuccess(_ -> log.info("Participant {} added to room {}", participant.getIdentity(), roomId))
                .then();
    }

    /**
     * Retrieves all participants in a given room.
     */
    public Flux<ParticipantDTO> findParticipantsByRoomId(String roomId) {
        String pattern = ROOM_CACHE_PREFIX + roomId + PARTICIPANT_CACHE_SUFFIX + "*";
        return participantTemplate.keys(pattern)
                .flatMap(participantOps::get);
    }

    /**
     * Removes a participant from a room's list in Redis.
     */
    public Mono<Void> removeParticipant(String roomId, String participantIdentity) {
        String key = ROOM_CACHE_PREFIX + roomId + PARTICIPANT_CACHE_SUFFIX + participantIdentity;
        return participantTemplate.delete(key)
                .doOnSuccess(_ -> log.info("Participant {} removed from room {}", participantIdentity, roomId))
                .then();
    }

    /**
     * Clears all participants for a given room.
     */
    public Mono<Void> clearParticipantsForRoom(String roomId) {
        String pattern = ROOM_CACHE_PREFIX + roomId + PARTICIPANT_CACHE_SUFFIX + "*";
        return participantTemplate.keys(pattern)
                .flatMap(participantTemplate::delete)
                .doOnComplete(() -> log.info("All participants cleared from room {}", roomId))
                .then();
    }
}
