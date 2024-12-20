package com.szampchat.server.event.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.shared.rabbitmq.RabbitMqProperties;
import com.szampchat.server.socket.data.EventOutboundMessage;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.*;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.time.Duration;

/**
 * Component that consumes events from {@link EventSink} and publishes them in rabbitmq
 */
@Component
@AllArgsConstructor
@Slf4j
class RabbitMqPublisher {

    private final Sender sender;
    private final EventSink eventSink;
    private final RouteMapper routeMapper;
    private final ObjectMapper objectMapper;
    private final RabbitMqProperties rabbitMqProperties;

    @PostConstruct
    private void init() {
        final String chatExchange = rabbitMqProperties.getChatExchange();
        var eventFlux = eventSink.asFlux()
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
                .doOnNext(outboundMessage -> log.debug("Publishing {}", outboundMessage.toString()));

        var sendMono = sender.send(eventFlux);

        configureRabbitMq().then(sendMono)
            .subscribe();
    }

    //Creates initial structure for rabbitmq (exchanges, bindings)
    private Mono<Void> configureRabbitMq() {
        //Initial configuration, preferably should be moved to different service, as this is publisher
        return sender.declare(
                ExchangeSpecification
                        .exchange(rabbitMqProperties.getChatExchange())
                        .durable(true)
                        .type("topic"))
                .then(sender.declare(
                        ExchangeSpecification
                                .exchange(rabbitMqProperties.getMessageExchange())
                                .durable(true)
                                .type("topic"))
                )
                //TODO maybe we can define bindings dynamically based on EventType
                .then(sender.bindExchange(
                        BindingSpecification
                                .binding()
                                .exchangeFrom(rabbitMqProperties.getChatExchange())
                                .exchangeTo(rabbitMqProperties.getMessageExchange())
                                .routingKey("messages.#"))
                ).then();
    }
}
