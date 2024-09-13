package com.szampchat.server.socket;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.event.MessageCreateEvent;
import com.szampchat.server.event.Recipient;
import com.szampchat.server.event.MessageEventBus;
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
    private final MessageEventBus messageEventBus;
    private final ChannelService channelService;
    private final RSocketPrincipalProvider rSocketPrincipalProvider;

    // Send a stream of events to the client
    @MessageMapping("/community/{communityId}/messages")
    public Flux<EventOutboundMessage<?>> streamEvents(@DestinationVariable Long communityId, @Payload Mono<EventRequestDTO> eventRequestDTO) {
        //I had to get principal manually, as providing it in method parameter caused error
        Recipient recipient = Recipient.fromCommunity(communityId);
        return rSocketPrincipalProvider.getPrincipal()
                .doOnNext(user -> log.debug("User {} subscribes to /community/{}/messages", user.getUserId(), communityId))
                //This part seems like it could slow down entire system
                // All events have to make a check for each user
                // Maybe MessageChannel should be used here
                //TODO spring integration's MessageChannels
                .flatMapMany(user -> messageEventBus.on(MessageCreateEvent.class)
                                //Those filters should be replaced by some routing function
                                .filter(event -> event.getRecipient().equals(recipient))
                                //ChannelService#isParticipant will be cached so it should hopefully work flawlessly
                                .filterWhen(event -> channelService.isParticipant(event.getData().getChannel(), user.getUserId()))
                                .map(EventOutboundMessage::fromSendEvent));


    }
}
