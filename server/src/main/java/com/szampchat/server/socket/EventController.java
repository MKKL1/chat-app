package com.szampchat.server.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Delivery;
import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.event.MessageCreateEvent;
import com.szampchat.server.event.Recipient;
import com.szampchat.server.event.EventPublisherService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.BindingSpecification;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Controller
public class EventController {
    private final ReceiverTemplate receiverTemplate;


    // Send a stream of events to the client
    @MessageMapping("/community/{communityId}/messages")
    public Flux<byte[]> streamEvents(@DestinationVariable Long communityId, @Payload Mono<EventRequestDTO> eventRequestDTO) {
        return receiverTemplate.receiveCommunityEvents(
                        communityId,
                        "chat.exchange.messages",
                        "messages.community." + communityId)
                .map(Delivery::getBody);
    }
}
