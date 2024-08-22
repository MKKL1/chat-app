package com.szampchat.server.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

public class CustomUser extends JwtAuthenticationToken {
    @Getter
    private final Long userId;

    public CustomUser(Jwt jwt, Collection<? extends GrantedAuthority> authorities, Long userId) {
        super(jwt, authorities);
        this.userId = userId;
    }
}
