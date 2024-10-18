package com.szampchat.server.auth;

import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@FunctionalInterface
public interface AuthorizationMethod {
    Mono<Boolean> apply(Mono<CurrentUser> currentUserMono, AuthorizationContext context);
}
