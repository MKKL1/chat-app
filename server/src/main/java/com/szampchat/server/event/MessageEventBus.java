package com.szampchat.server.event;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

/**
 * Publisher of message events, which can be later consumed by required message streams
 */
@Slf4j
public class MessageEventBus {

    private final int bufferSize = 10;
    private final Scheduler scheduler = Schedulers.boundedElastic();

    private final Sinks.EmitFailureHandler emitFailureHandler = (_, emitResult) -> emitResult.equals(Sinks.EmitResult.FAIL_NON_SERIALIZED);
    private final Sinks.Many<MessageEvent<?>> events = Sinks.many().multicast().onBackpressureBuffer(bufferSize, false);

    public void publish(MessageEvent<?> event) {
        log.debug("Publishing event {}", event);
        events.emitNext(event, emitFailureHandler);
    }

    public <E extends MessageEvent<?>> Flux<E> on(Class<E> clazz) {
        return events.asFlux().publishOn(scheduler).ofType(clazz);
    }

    //TODO method for subscribing to events from given recipient
}
