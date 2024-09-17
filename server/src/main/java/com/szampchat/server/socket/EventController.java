package com.szampchat.server.socket;

import com.rabbitmq.client.Delivery;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
