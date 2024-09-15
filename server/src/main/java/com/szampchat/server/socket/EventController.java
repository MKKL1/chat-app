package com.szampchat.server.socket;

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
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

@Slf4j
@AllArgsConstructor
@Controller
public class EventController {
    private final EventPublisherService eventSender;
    private final ChannelService channelService;
    private final RSocketPrincipalProvider rSocketPrincipalProvider;
    private final RabbitMqReceiverService rabbitMqReceiverService;
    private final Sender sender;

    // Send a stream of events to the client
    @MessageMapping("/community/{communityId}/messages")
    public Flux<byte[]> streamEvents(@DestinationVariable Long communityId, @Payload Mono<EventRequestDTO> eventRequestDTO) {
        //I had to get principal manually, as providing it in method parameter caused error
        return rSocketPrincipalProvider.getPrincipal()
                .doOnNext(user -> log.debug("User {} subscribes to /community/{}/messages", user.getUserId(), communityId))
                .flatMapMany(currentUser -> {
                    String queue = "user." + currentUser.getUserId();
                    return sender.declare(QueueSpecification.queue(queue).autoDelete(true))
                            .thenMany(rabbitMqReceiverService.getOrCreateConsumer(queue))
                            .map(Delivery::getBody);

                });
    }
}
