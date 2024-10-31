package com.szampchat.server.voice;

import com.szampchat.server.voice.livekit.LiveKitEventService;
import com.szampchat.server.voice.livekit.events.RoomFinishedEvent;
import jakarta.annotation.PostConstruct;
import livekit.LivekitModels;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

//I think it could be called repository, but I am not sure
@Slf4j
@AllArgsConstructor
@Repository
public class LivekitRepository {
    private final LiveKitEventService liveKitEventService;
    private final Map<String, LivekitModels.Room> roomCache = new ConcurrentHashMap<>();

    @PostConstruct
    private void startLivekitListener() {
        liveKitEventService.on(RoomFinishedEvent.class)
                .mapNotNull(roomFinishedEvent -> roomCache.remove(roomFinishedEvent.getRoom().getName()))
                .doOnNext(room -> log.info("Room removed from cache {}", room.getName()))
                .subscribe();
    }

    public Mono<Void> addRoom(Long channelId, LivekitModels.Room room) {
        return Mono.fromRunnable(() -> {
            roomCache.put(channelId.toString(), room);
        });
    }

    public Mono<LivekitModels.Room> findRoom(Long channelId) {
        return Mono.justOrEmpty(roomCache.get(channelId.toString()));
    }

}
