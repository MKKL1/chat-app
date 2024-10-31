package com.szampchat.server.voice.livekit;

import com.szampchat.server.voice.livekit.event.RoomFinishedEvent;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class LivekitEventListener {
    private final LiveKitEventService liveKitEventService;
    private final LivekitRepository livekitRepository;

    @PostConstruct
    private void startLivekitListener() {
        liveKitEventService.on(RoomFinishedEvent.class)
                .flatMap(roomFinishedEvent ->
                        livekitRepository.removeRoom(roomFinishedEvent.getRoom().getName())
                                .doOnNext(room -> log.info("Room removed from cache {}", roomFinishedEvent.getRoom().getName()))
                )
                .subscribe();
    }
}
