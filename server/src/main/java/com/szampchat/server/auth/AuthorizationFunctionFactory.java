package com.szampchat.server.auth;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.role.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class AuthorizationFunctionFactory {
    private final ChannelService channelService;
    private final CommunityMemberService communityMemberService;
    private final CommunityService communityService;
    private final RoleService roleService;

    public final AuthorizationMethod isParticipant = new AuthorizationMethod() {
        @Override
        public Mono<Boolean> apply(Mono<CurrentUser> currentUserMono, AuthorizationContext context) {
            return currentUserMono.flatMap(user -> {
                Object channelIdObject = context.getVariables().get("channelId");
                if(!(channelIdObject instanceof Long)) {
                    return Mono.error(new IllegalArgumentException());
                }
                return channelService.isParticipant((Long) channelIdObject, user.getUserId());
            });

        }
    };

    public final AuthorizationMethod isMember = new AuthorizationMethod() {
        @Override
        public Mono<Boolean> apply(Mono<CurrentUser> currentUserMono, AuthorizationContext context) {
            return currentUserMono.flatMap(user -> {
                Object communityIdObject = context.getVariables().get("communityId");
                if(!(communityIdObject instanceof Long)) {
                    return Mono.error(new IllegalArgumentException());
                }
                return communityMemberService.isMember((Long) communityIdObject, user.getUserId());
            });
        }
    };

    public final AuthorizationMethod isNotMember = (currentUserMono, context) -> isMember.apply(currentUserMono, context).map(val -> !val);

    public final AuthorizationMethod isOwner = new AuthorizationMethod() {
        @Override
        public Mono<Boolean> apply(Mono<CurrentUser> currentUserMono, AuthorizationContext context) {
            return currentUserMono.flatMap(user -> {
                Object communityIdObject = context.getVariables().get("communityId");
                if(!(communityIdObject instanceof Long)) {
                    return Mono.error(new IllegalArgumentException());
                }
                return communityService.isOwner((Long) communityIdObject, user.getUserId());
            });
        }
    };

    public final AuthorizationMethod hasAccessToRoleInfo = new AuthorizationMethod() {
        @Override
        public Mono<Boolean> apply(Mono<CurrentUser> currentUserMono, AuthorizationContext context) {
            return currentUserMono.flatMap(user -> {
                Object roleIdObject = context.getVariables().get("roleId");
                if(!(roleIdObject instanceof Long)) {
                    return Mono.error(new IllegalArgumentException());
                }
                return roleService.hasAccessToRoleInfo((Long) roleIdObject, user.getUserId());
            });
        }
    };
}
