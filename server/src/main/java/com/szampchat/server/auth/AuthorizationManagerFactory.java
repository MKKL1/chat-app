package com.szampchat.server.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private ObjectMapper mapper = new ObjectMapper();

    private Mono<CurrentUser> getCurrentUser(Authentication authentication) {
        return Mono.just(authentication.getPrincipal())
                .switchIfEmpty(Mono.error(new IllegalStateException("Principal is empty")))
                .filter(principal -> principal instanceof Jwt)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not jwt"))) //Not sure if it is even possible to happen
                .flatMap(jwt -> converter.convert((Jwt) jwt));
    }

    public ReactiveAuthorizationManager<AuthorizationContext> create(AuthorizationMethod preauthorize,
                                                                     PermissionContext permissionContext,
                                                                     PermissionFlag... permissionFlags) {

        return (authenticationMono, context) ->
                authenticationMono
                .filter(Authentication::isAuthenticated)
                .flatMap(authentication -> preauthorize.apply(getCurrentUser(authentication), context)
                        .flatMap(authorized -> {
                            if (!authorized) {
                                log.info("Failed authorization. Reason: {}", preauthorize.logMessage());
                                return Mono.just(new AuthorizationDecision(false));
                            }


                            Mono<Boolean> permissionAuthorizedMono = switch (permissionContext) {
                                case CHANNEL ->
                                        permissionService.hasPermissionInChannel(AuthorizationContextExtractor.getChannelId(context), permissionFlags);
                                case COMMUNITY ->
                                        permissionService.hasPermissionInCommunity(AuthorizationContextExtractor.getCommunityId(context), permissionFlags);
                                //TODO add default case
                                default -> Mono.just(false);
                            };

                            return permissionAuthorizedMono
                                    .doOnNext(permissionAuthorized -> {
                                        if (!permissionAuthorized)
                                            log.info("Failed authorization. Reason: No permissions {}", (Object) permissionFlags);
                                    })
                                    .flatMap(permissionAuthorized -> Mono.just(new AuthorizationDecision(permissionAuthorized)));
                        }));
    }

    public ReactiveAuthorizationManager<AuthorizationContext> create(AuthorizationMethod preauthorize) {
        return (authenticationMono, context) ->
                authenticationMono.flatMap(auth -> preauthorize.apply(getCurrentUser(auth), context))
                .flatMap(authorized -> Mono.just(new AuthorizationDecision(authorized)));
    }
}
