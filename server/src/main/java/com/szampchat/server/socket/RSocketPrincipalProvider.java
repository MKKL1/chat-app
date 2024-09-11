package com.szampchat.server.socket;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.auth.CustomJwtAuthenticationConverter;
import com.szampchat.server.auth.UserNotRegisteredException;
import lombok.AllArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class RSocketPrincipalProvider {
    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    public Mono<CurrentUser> getPrincipal() {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication().getPrincipal())
                .filter(principal -> principal instanceof Jwt)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Not jwt"))) //Not sure if it is even possible to happen
                .flatMap(jwt -> customJwtAuthenticationConverter.convert((Jwt) jwt));
    }
}
