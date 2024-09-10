package com.szampchat.server.socket;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.event.AbstractEvent;
import com.szampchat.server.event.EventBus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.time.Duration;

@Slf4j
@AllArgsConstructor
@Controller
public class EventController {


    private final EventBus eventBus;

    // Send a stream of events to the client
    @MessageMapping("events.stream")
    public Flux<String> streamEvents(@Payload Mono<EventRequestDTO> eventRequestDTO) { //Intent can be stated here, maybe
//        log.info("Streaming to user {}", principal.getName());
        return Flux
                .interval(Duration.ofSeconds(1))
                .map(index -> "Message" + index);

    }
}
