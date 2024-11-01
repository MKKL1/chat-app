package com.szampchat.server.livekit.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LiveKitAuthManager implements ReactiveAuthenticationManager {
    private final String key;
    private final String secret;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .map(Authentication::getCredentials)
                .cast(String.class)
                .filter(this::validate)
                .map(_ -> authentication)
                .switchIfEmpty(Mono.error(new InvalidBearerTokenException("")));
    }

    private boolean validate(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier decodedJWT = JWT.require(algorithm)
                .withIssuer(key)
                .build();

        try {
            decodedJWT.verify(token);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
