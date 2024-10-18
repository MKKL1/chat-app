package com.szampchat.server.auth;

import com.szampchat.server.permission.PermissionService;
import com.szampchat.server.permission.data.PermissionContext;
import com.szampchat.server.permission.data.PermissionFlag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
@Component
public class AuthorizationManagerFactory {
    private final CustomJwtAuthenticationConverter converter;
    private final PermissionService permissionService;

    //Hell
    private Mono<CurrentUser> getCurrentUser(Mono<Authentication> authenticationMono) {
        return authenticationMono
                .map(Authentication::getPrincipal)
                .doOnNext(principal -> log.info("Principal {}", principal))
                .filter(principal -> principal instanceof Jwt)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not jwt"))) //Not sure if it is even possible to happen
                .flatMap(jwt -> converter.convert((Jwt) jwt));
    }

    public ReactiveAuthorizationManager<AuthorizationContext> create(AuthorizationMethod preauthorize,
                                                                     PermissionContext permissionContext,
                                                                     PermissionFlag... permissionFlags) {
        return (authentication, context) ->
                preauthorize.apply(getCurrentUser(authentication), context)
                .flatMap(authorized -> {
                    if(!authorized)
                        return Mono.just(new AuthorizationDecision(false));

                    Mono<Boolean> permissionAuthorizedMono = switch (permissionContext)
                    {
                        case CHANNEL -> permissionService.hasPermissionInChannel(AuthorizationContextExtractor.getChannelId(context), permissionFlags);
                        case COMMUNITY -> permissionService.hasPermissionInChannel(AuthorizationContextExtractor.getCommunityId(context), permissionFlags);
                    };

                    return permissionAuthorizedMono
                            .flatMap(permissionAuthorized -> Mono.just(new AuthorizationDecision(permissionAuthorized)));
                });
    }

    public ReactiveAuthorizationManager<AuthorizationContext> create(AuthorizationMethod preauthorize) {
        return (authentication, context) ->
                preauthorize.apply(getCurrentUser(authentication), context)
                .flatMap(authorized -> Mono.just(new AuthorizationDecision(authorized)));
    }
}
