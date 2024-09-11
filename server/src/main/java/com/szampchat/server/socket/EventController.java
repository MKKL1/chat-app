package com.szampchat.server.socket;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.auth.UserNotRegisteredException;
import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.event.MessageCreateEvent;
import com.szampchat.server.event.Recipient;
import com.szampchat.server.event.EventBus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Controller
public class EventController {
    private final EventBus eventBus;
    private final ChannelService channelService;
    private final RSocketPrincipalProvider rSocketPrincipalProvider;

    // Send a stream of events to the client
    @MessageMapping("/community/{communityId}/messages")
    public Flux<EventMessage<?>> streamEvents(@DestinationVariable Long communityId, @Payload Mono<EventRequestDTO> eventRequestDTO) {
        //I had to get principal manually, as providing it in method parameter caused error
        return rSocketPrincipalProvider.getPrincipal()
                //This part seems like it could slow down entire system
                // All events have to make a check for each user
                // Maybe MessageChannel should be used here
                .flatMapMany(user -> eventBus.on(MessageCreateEvent.class)
                                //Those filter should be replaced by some routing function
                                .filter(event ->
                                        event.getRecipient().getContext() == Recipient.Context.COMMUNITY &&
                                                event.getRecipient().getId() == communityId)
                                //ChannelService#isParticipant will be cached so it should hopefully work flawlessly
                                .filterWhen(event -> channelService.isParticipant(event.getData().getChannel(), user.getUserId()))
                                .map(EventMessage::fromSendEvent));


    }
}
