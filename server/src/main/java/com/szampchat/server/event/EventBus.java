package com.szampchat.server.event;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class EventBus {

    private final int bufferSize = 10;
    private final Scheduler scheduler = Schedulers.boundedElastic();

    private final Sinks.EmitFailureHandler emitFailureHandler = (_, emitResult) -> emitResult.equals(Sinks.EmitResult.FAIL_NON_SERIALIZED);
    private final Sinks.Many<AbstractEvent> events = Sinks.many().multicast().onBackpressureBuffer(bufferSize, false);

    public void publish(AbstractEvent event) {
        log.debug(STR."Publishing event \{event}");
        events.emitNext(event, emitFailureHandler);
    }

    public <E extends AbstractEvent> Flux<E> on(Class<E> clazz) {
        return events.asFlux().publishOn(scheduler).ofType(clazz);
    }
}
