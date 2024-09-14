package com.szampchat.server.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.Sender;

/**
 * Publisher of message events, which can be later consumed by required message streams
 */
@Slf4j
@Service
public class EventPublisherService {

    private final int bufferSize = 10;
    private final Scheduler scheduler = Schedulers.boundedElastic();

    private final Sinks.EmitFailureHandler emitFailureHandler = (_, emitResult) -> emitResult.equals(Sinks.EmitResult.FAIL_NON_SERIALIZED);
    private final Sinks.Many<MessageEvent<?>> events = Sinks.many().multicast().onBackpressureBuffer(bufferSize, false);

    public void publish(MessageEvent<?> event) {
        log.debug("Publishing event {}", event);
        events.emitNext(event, emitFailureHandler);
    }

    public Flux<MessageEvent<?>> asFlux() {
        return events.asFlux().publishOn(scheduler);
    }
}
