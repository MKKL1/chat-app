package com.szampchat.server.event;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

@Component
@AllArgsConstructor
public class RabbitMqPublisher {

    private final Sender sender;
    private final EventPublisherService eventPublisherService;

    @PostConstruct
    private void init() {
        sender.send(eventPublisherService.asFlux()
                .map(event -> new OutboundMessage())
        );
    }
}
