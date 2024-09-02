package com.szampchat.server;

import com.szampchat.server.auth.CustomJwtAuthenticationConverter;
import com.szampchat.server.auth.UserNotRegisteredException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@EnableWebFluxSecurity
@Configuration
@EnableReactiveMethodSecurity
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
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityWebFilterChain securityFilterChainForUserCreation(ServerHttpSecurity http) {
        return http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST ,"/users"))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(apiConfigurationSource()))
                .authorizeExchange(auth -> auth.anyExchange().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .build();
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .addFilterBefore(new CustomAuthenticationFilter(userService), UsernamePasswordAuthenticationFilter.class)
                .authorizeExchange(auth -> auth
                        .pathMatchers(WHITELIST).permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(customJwtAuthenticationConverter)
                        )
                        .authenticationEntryPoint(userNotRegisteredExceptionEntryPoint())
                )
                .build();
    }

    public ServerAuthenticationEntryPoint userNotRegisteredExceptionEntryPoint() {
        return (exchange, ex) -> {
            if (ex instanceof UserNotRegisteredException) {
                exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(419));
                return Mono.empty();
            }
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(401));
            return Mono.empty();
        };
    }

    // Global cors config
    @Bean
    CorsConfigurationSource apiConfigurationSource(){
        CorsConfiguration conf = new CorsConfiguration();
        conf.setAllowedOrigins(List.of("http://localhost:4200"));
        conf.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        conf.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }
}
