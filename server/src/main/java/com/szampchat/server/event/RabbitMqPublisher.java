package com.szampchat.server.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

@Component
@AllArgsConstructor
public class RabbitMqPublisher {

    private final Sender sender;
    private final EventPublisherService eventPublisherService;
    private final RecipientMapper recipientMapper;
    private final ObjectMapper objectMapper;
    private final RabbitMqProperties rabbitMqProperties;

    @PostConstruct
    private void init() {
        final String chatExchange = rabbitMqProperties.getChatExchange();

        //Make sure that exchange exists
        sender.declare(ExchangeSpecification.exchange(chatExchange).durable(true).type("direct"))
            .thenMany(sender.send(eventPublisherService.asFlux()
                    .flatMap(event -> {
                                byte[] buf;
                            try {
                                buf = objectMapper.writeValueAsBytes(event.getData());
                            } catch (JsonProcessingException e) {
                                return Mono.error(e);
                            }
                            return Mono.just(new OutboundMessage(
                                chatExchange,
                                recipientMapper.toRoute(event.getRecipient()),
                                buf
                            ));
                    }
                    )
            )).subscribe();
    }
}
