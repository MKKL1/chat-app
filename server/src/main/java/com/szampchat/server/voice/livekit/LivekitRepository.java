package com.szampchat.server.voice.livekit;

import com.szampchat.server.voice.livekit.dto.RoomDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


//I think it could be called repository, but I am not sure
@Slf4j
@Repository
public class LivekitRepository {
    private final ReactiveValueOperations<String, RoomDTO> roomOps;
    private static final String ROOM_CACHE_PREFIX = "room:";

    public LivekitRepository(ReactiveRedisTemplate<String, RoomDTO> roomTemplate) {
        roomOps = roomTemplate.opsForValue();
    }

    public Mono<Boolean> addRoom(String channelId, RoomDTO room) {
        return roomOps.set(idToKey(channelId), room);
    }

    public Mono<RoomDTO> findRoom(String channelId) {
        return roomOps.get(idToKey(channelId));
    }

    public Mono<Boolean> removeRoom(String channelId) {
        return roomOps.delete(idToKey(channelId));
    }

    private String idToKey(String channelId) {
        return ROOM_CACHE_PREFIX + channelId;
    }
}
