package com.szampchat.server.auth.handler;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.auth.ResourceTypes;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class ChannelAccessHandler implements ResourceAccessHandler {
    private final ChannelService channelService;

    @Override
    public ResourceTypes getType() {
        return ResourceTypes.CHANNEL;
    }

    @Override
    public Mono<Boolean> hasAccess(long resourceId, long userId) {
        return channelService.isParticipant(resourceId, userId);
    }
}
