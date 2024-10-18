package com.szampchat.server.auth;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.role.RoleService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
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
            return currentUserMono.flatMap(user -> channelService.isParticipant(AuthorizationContextExtractor.getChannelId(context), user.getUserId()));
        }

        @Override
        public String logMessage() {
            return "User is not participant of channel";
        }
    };

    public final AuthorizationMethod isMember = new AuthorizationMethod() {
        @Override
        public Mono<Boolean> apply(Mono<CurrentUser> currentUserMono, AuthorizationContext context) {
            return currentUserMono.flatMap(user -> communityMemberService.isMember(AuthorizationContextExtractor.getCommunityId(context), user.getUserId()));
        }

        @Override
        public String logMessage() {
            return "User is not member of community";
        }
    };

    public final AuthorizationMethod isNotMember = (currentUserMono, context) -> isMember.apply(currentUserMono, context).map(val -> !val);

    public final AuthorizationMethod isOwner = new AuthorizationMethod() {
        @Override
        public Mono<Boolean> apply(Mono<CurrentUser> currentUserMono, AuthorizationContext context) {
            return currentUserMono.flatMap(user -> communityService.isOwner(AuthorizationContextExtractor.getCommunityId(context), user.getUserId()));
        }

        @Override
        public String logMessage() {
            return "User is not owner of community";
        }
    };

    public final AuthorizationMethod hasAccessToRoleInfo = new AuthorizationMethod() {
        @Override
        public Mono<Boolean> apply(Mono<CurrentUser> currentUserMono, AuthorizationContext context) {
            return currentUserMono.flatMap(user -> roleService.hasAccessToRoleInfo(AuthorizationContextExtractor.getRoleId(context), user.getUserId()));
        }

        @Override
        public String logMessage() {
            return "User is not member of community (role)";
        }
    };
}
