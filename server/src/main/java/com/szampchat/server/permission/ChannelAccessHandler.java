package com.szampchat.server.permission;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.permission.data.ResourceTypes;
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
