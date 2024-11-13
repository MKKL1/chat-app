package com.szampchat.server.permission;

import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.permission.data.ResourceTypes;
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
