package com.szampchat.server;

import com.szampchat.server.auth.preauth.HasPermAuthManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@Profile(value = {"test"})
@TestConfiguration
public class TestSecurityConfiguration {

    @Bean
    public SecurityWebFilterChain testSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .anyExchange().permitAll()
                )
                .build();
    }

    @Bean
    public ReactiveAuthorizationManager<AuthorizationContext> defaultAuthorizationManager() {
        return (authentication, context) -> Mono.just(new AuthorizationDecision(true));
    }

    @Primary
    @Bean
    public HasPermAuthManager hasPermAuthManager() {
        return (authentication, context) -> Mono.just(new AuthorizationDecision(true));
    }
}
