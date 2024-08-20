package com.szampchat.server;

import com.szampchat.server.auth.CustomJwtAuthenticationConverter;
import com.szampchat.server.auth.UserNotRegisteredException;
import com.szampchat.server.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@Configuration
//@EnableMethodSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfig {

    @Autowired
    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    private static final String[] WHITELIST = {
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/error",
            "/auth/register",
    };

    @Bean
    @Order(1)
    public SecurityWebFilterChain securityFilterChainForUserCreation(ServerHttpSecurity http) throws Exception {
        return http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST ,"/api/users"))
                .authorizeExchange(auth -> auth.anyExchange().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
//                .sessionManagement(session -> session.concurrentSessions())
                .build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http, UserService userService) throws Exception {
        return http
                .csrf(csrfSpec -> csrfSpec.disable())
//                .addFilterBefore(new CustomAuthenticationFilter(userService), UsernamePasswordAuthenticationFilter.class)
                .authorizeExchange(auth -> auth
                        .pathMatchers(WHITELIST).permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(customJwtAuthenticationConverter)
                        )
                )
                .exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec.authenticationEntryPoint(serverAuthenticationEntryPoint()))
                .build();
    }

    public ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
        return (exchange, ex) -> Mono.just(ex)
                .doOnNext(ex2 -> log.info("ServerAuthenticationEntryPoint: " + ex2))
                    .filter(e -> e instanceof UserNotRegisteredException)
                    .doOnNext(_ -> exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(419)))
                .then();
    }
}
