package com.szampchat.server.auth;

import com.szampchat.server.permission.PermissionService;
import com.szampchat.server.permission.data.PermissionContext;
import com.szampchat.server.permission.data.PermissionFlag;
import lombok.AllArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;
import java.util.function.Function;

@AllArgsConstructor
@Component
public class AuthorizationManagerFactory {
    private final CustomJwtAuthenticationConverter converter;
    private final PermissionService permissionService;

    //Hell
    private final Function<Mono<Authentication>, Mono<CurrentUser>> currentUserProvider =
            (authentication) -> authentication
            .map(Authentication::getPrincipal)
            .filter(principal -> principal instanceof Jwt)
            .switchIfEmpty(Mono.error(new IllegalArgumentException("Not jwt"))) //Not sure if it is even possible to happen
            .flatMap(jwt -> converter.convert((Jwt) jwt));


    public ReactiveAuthorizationManager<AuthorizationContext> create(BiFunction<Mono<CurrentUser>, AuthorizationContext, Mono<Boolean>> preauthorize,
                                                                     PermissionContext permissionContext,
                                                                     PermissionFlag... permissionFlags) {
        return (authentication, context) ->
                preauthorize.apply(currentUserProvider.apply(authentication), context)
                .flatMap(authorized -> {
                    if(!authorized)
                        return Mono.just(new AuthorizationDecision(false));

                    Mono<Boolean> permissionAuthorizedMono = Mono.just(false);
                    switch (permissionContext)
                    {
                        case CHANNEL -> {
                            Object channelIdObject = context.getVariables().get("channelId");
                            if(!(channelIdObject instanceof Long)) {
                                return Mono.error(new IllegalArgumentException());
                            }
                            permissionAuthorizedMono = permissionService.hasPermissionInChannel((Long) channelIdObject, permissionFlags);
                        }
                        case COMMUNITY -> {
                            Object communityIdObject = context.getVariables().get("communityId");
                            if(!(communityIdObject instanceof Long)) {
                                return Mono.error(new IllegalArgumentException());
                            }
                            permissionAuthorizedMono = permissionService.hasPermissionInChannel((Long) communityIdObject, permissionFlags);
                        }
                    }

                    return permissionAuthorizedMono
                            .flatMap(permissionAuthorized -> Mono.just(new AuthorizationDecision(permissionAuthorized)));
                });
    }

    public ReactiveAuthorizationManager<AuthorizationContext> create(BiFunction<Mono<CurrentUser>, AuthorizationContext, Mono<Boolean>> preauthorize) {
        return (authentication, context) ->
                preauthorize.apply(currentUserProvider.apply(authentication), context)
                .flatMap(authorized -> Mono.just(new AuthorizationDecision(authorized)));
    }
}
