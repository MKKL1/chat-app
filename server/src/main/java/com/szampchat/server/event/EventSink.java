package com.szampchat.server.event;

import com.szampchat.server.event.data.InternalEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Publisher of events, which can be later consumed by required event streams
 */
@Slf4j
@Component
public class EventSink {

    //Those variables could be defined somewhere else for easier configuration, but I am not sure where
    private final int bufferSize = 10;
    private final Scheduler scheduler = Schedulers.boundedElastic();

    //Reactor stuff
    private final Sinks.EmitFailureHandler emitFailureHandler = (_, emitResult) -> emitResult.equals(Sinks.EmitResult.FAIL_NON_SERIALIZED);
    private final Sinks.Many<InternalEvent<?>> events = Sinks.many().multicast().onBackpressureBuffer(bufferSize, false);
    
    public void publish(InternalEvent<?> event) {
        log.info("Publishing event {}", event);
        events.emitNext(event, emitFailureHandler);
    }

    /**
     * @return Flux of internal events
     */
    public Flux<InternalEvent<?>> asFlux() {
        return events.asFlux().publishOn(scheduler);
    }
}
