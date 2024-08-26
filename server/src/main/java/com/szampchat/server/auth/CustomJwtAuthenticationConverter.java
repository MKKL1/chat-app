package com.szampchat.server.auth;

import com.szampchat.server.user.UserService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, Mono<CurrentUser>> {

    private final UserService userService;

    public CustomJwtAuthenticationConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<CurrentUser> convert(Jwt jwt) {
        return Mono.fromCallable(jwt::getSubject)
                .flatMap(keycloakId -> userService.findUserBySub(UUID.fromString(keycloakId)))
                .switchIfEmpty(Mono.error(new UserNotRegisteredException()))
                .map(user -> new CurrentUser(jwt, null, user.getId()));
    }
}
