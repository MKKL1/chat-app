package com.szampchat.server.voice.repository;

import com.szampchat.server.livekit.dto.RoomDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface RoomRepository {
    Mono<Boolean> addRoom(String roomId, RoomDTO room);
    Mono<RoomDTO> findRoom(String roomId);
    Flux<RoomDTO> findRooms(Collection<String> roomIds);
    Mono<Boolean> removeRoom(String roomId);
}
