package com.szampchat.server.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szampchat.server.socket.EventOutboundMessage;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.BindingSpecification;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

@Component
@AllArgsConstructor
@Slf4j
public class RabbitMqPublisher {

    private final Sender sender;
    private final EventPublisherService eventPublisherService;
    private final RouteMapper routeMapper;
    private final ObjectMapper objectMapper;
    private final RabbitMqProperties rabbitMqProperties;

    @PostConstruct
    private void init() {
        final String chatExchange = rabbitMqProperties.getChatExchange();

        //Initial configuration, preferably should be moved to different service, as this is publisher
//        sender.declare(ExchangeSpecification.exchange(chatExchange).durable(true).type("topic"))
//                .then(sender.declare(ExchangeSpecification.exchange(rabbitMqProperties.getMessageExchange()).durable(true).type("direct")))
//                //sender.bind(BindingSpecification.exchangeBinding(chatExchange, "messages.#", rabbitMqProperties.getMessageExchange()))
//                .then(sender.bind(BindingSpecification.binding().exchangeFrom(chatExchange).exchangeTo(rabbitMqProperties.getMessageExchange()).routingKey("messages.#")))
            sender.send(eventPublisherService.asFlux()
                    .flatMap(event -> {
                                byte[] buf;
                            try {
                                buf = objectMapper.writeValueAsBytes(EventOutboundMessage.fromSendEvent(event));
                            } catch (JsonProcessingException e) {
                                return Mono.error(e);
                            }
                            return Mono.just(new OutboundMessage(
                                chatExchange,
                                routeMapper.toRoute(event.getRecipient()),
                                buf
                            ));
                    })
                    .doOnNext(outboundMessage -> log.info("Publishing {}", outboundMessage.toString()))
            ).subscribe();
    }
}
