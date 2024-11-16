package com.szampchat.server.auth.handler;

import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.auth.ResourceTypes;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class CommunityAccessHandler implements ResourceAccessHandler {
    private final CommunityMemberService communityMemberService;

    @Override
    public ResourceTypes getType() {
        return ResourceTypes.COMMUNITY;
    }

    @Override
    public Mono<Boolean> hasAccess(long resourceId, long userId) {
        return communityMemberService.isMember(resourceId, userId);
    }
}
