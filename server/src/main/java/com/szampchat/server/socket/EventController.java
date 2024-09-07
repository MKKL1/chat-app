package com.szampchat.server.socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Controller
public class EventController {

    // Send a stream of events to the client
    @MessageMapping("events.stream")
    public Flux<String> streamEvents(Mono<String> clientMessage) {
        return clientMessage
                .doOnNext(msg -> System.out.println("Received from client: " + msg))
                .thenMany(Flux.interval(Duration.ofSeconds(1))
                        .map(interval -> "Event: " + interval));
    }
}
