package com.szampchat.server.event.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szampchat.server.event.EventSinkService;
import com.szampchat.server.socket.data.EventOutboundMessage;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.BindingSpecification;
import reactor.rabbitmq.ExchangeSpecification;
import reactor.rabbitmq.OutboundMessage;
import reactor.rabbitmq.Sender;

/**
 * Component that consumes events from {@link EventSinkService} and publishes them in rabbitmq
 */
@Component
@AllArgsConstructor
@Slf4j
class RabbitMqPublisher {

    private final Sender sender;
    private final EventSinkService eventSinkService;
    private final RouteMapper routeMapper;
    private final ObjectMapper objectMapper;
    private final RabbitMqProperties rabbitMqProperties;

    @PostConstruct
    private void init() {
        final String chatExchange = rabbitMqProperties.getChatExchange();
        configureRabbitMq().then(
            sender.send(eventSinkService.asFlux()
                    .flatMap(event -> {

                        //Convert event data to byte buffer
                        byte[] buf;
                        try {
                            buf = objectMapper.writeValueAsBytes(EventOutboundMessage.fromSendEvent(event));
                        } catch (JsonProcessingException e) {
                            return Mono.error(e);
                        }

                        //Publish to main exchange
                        return Mono.just(new OutboundMessage(
                            chatExchange,
                            routeMapper.toRouteKey(event),
                            buf
                        ));
                    })
                    .doOnNext(outboundMessage -> log.debug("Publishing {}", outboundMessage.toString()))
            )).subscribe();
    }

    //Creates initial structure for rabbitmq (exchanges, bindings)
    private Mono<Void> configureRabbitMq() {
        //Initial configuration, preferably should be moved to different service, as this is publisher
        return sender.declare(ExchangeSpecification.exchange(rabbitMqProperties.getChatExchange()).durable(true).type("topic"))
                .then(sender.declare(ExchangeSpecification.exchange(rabbitMqProperties.getMessageExchange()).durable(true).type("topic")))
                //TODO maybe we can define bindings dynamically based on EventType
                .then(sender.bindExchange(BindingSpecification.binding().exchangeFrom(rabbitMqProperties.getChatExchange()).exchangeTo(rabbitMqProperties.getMessageExchange()).routingKey("messages.#")))
                .then();
    }
}
