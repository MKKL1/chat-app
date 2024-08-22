package com.szampchat.server.auth;

import com.szampchat.server.user.UserService;
import com.szampchat.server.user.entity.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, Mono<CustomUser>> {

    private final UserService userService;

    public CustomJwtAuthenticationConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<CustomUser> convert(Jwt jwt) {
        return Mono.fromCallable(jwt::getSubject)
                .flatMap(keycloakId -> userService.findUserBySub(UUID.fromString(keycloakId)))
                .switchIfEmpty(Mono.error(new UserNotRegisteredException()))
                .map(user -> new CustomUser(jwt, null, user.getId()));
    }
}
