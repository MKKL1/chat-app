package com.szampchat.server.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;

public class CurrentUser extends JwtAuthenticationToken {
    @Getter
    private final Long userId;

    public CurrentUser(Jwt jwt, Collection<? extends GrantedAuthority> authorities, Long userId) {
        super(jwt, authorities);
        this.userId = userId;
    }
}
