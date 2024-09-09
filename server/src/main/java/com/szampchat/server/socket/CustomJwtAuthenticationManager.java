package com.szampchat.server.socket;

import com.szampchat.server.auth.CustomJwtAuthenticationConverter;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomJwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final CustomJwtAuthenticationConverter jwtAuthenticationConverter;

    public CustomJwtAuthenticationManager(CustomJwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken) {
            Jwt jwt = ((JwtAuthenticationToken) authentication).getToken();
            return jwtAuthenticationConverter.convert(jwt)
                    .map(currentUser -> new JwtAuthenticationToken(jwt, null, currentUser.getName()));
        }
        return Mono.empty();
    }
}