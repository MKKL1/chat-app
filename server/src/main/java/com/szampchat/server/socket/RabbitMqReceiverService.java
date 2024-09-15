package com.szampchat.server.socket;

import com.rabbitmq.client.Delivery;
import com.szampchat.server.event.MessageEvent;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.rabbitmq.Receiver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Service
public class RabbitMqReceiverService {

    private final Map<String, Flux<Delivery>> queueToEventFluxMap = new ConcurrentHashMap<>();
    private final Receiver receiver;

    public Flux<Delivery> getOrCreateConsumer(String queue) {
        Flux<Delivery> deliveryFlux = queueToEventFluxMap.get(queue);
        if(deliveryFlux == null) deliveryFlux = receiver.consumeAutoAck(queue);
        queueToEventFluxMap.put(queue, deliveryFlux);
        return deliveryFlux
                .doOnComplete(() -> queueToEventFluxMap.remove(queue));
    }
}
