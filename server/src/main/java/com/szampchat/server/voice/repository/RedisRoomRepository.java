package com.szampchat.server.voice.repository;

import com.szampchat.server.livekit.dto.RoomDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;


//I think it could be called repository, but I am not sure
@Slf4j
@Repository
public class RedisRoomRepository implements RoomRepository{
    private final ReactiveValueOperations<String, RoomDTO> roomOps;
    private static final String ROOM_CACHE_PREFIX = "room:";

    public RedisRoomRepository(ReactiveRedisTemplate<String, RoomDTO> roomTemplate) {
        roomOps = roomTemplate.opsForValue();
    }

    public Mono<Boolean> addRoom(String roomId, RoomDTO room) {
        return roomOps.set(idToKey(roomId), room);
    }

    public Mono<RoomDTO> findRoom(String roomId) {
        return roomOps.get(idToKey(roomId));
    }

    public Flux<RoomDTO> findRooms(Collection<String> roomIds) {
        return roomOps.multiGet(roomIds)
                .flatMapMany(Flux::fromIterable);
    }

    public Mono<Boolean> removeRoom(String roomId) {
        return roomOps.delete(idToKey(roomId));
    }

    private String idToKey(String roomId) {
        return ROOM_CACHE_PREFIX + roomId;
    }
}
