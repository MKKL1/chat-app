package com.szampchat.server;

import com.szampchat.server.auth.CustomJwtAuthenticationConverter;
import com.szampchat.server.auth.exception.UserNotRegisteredException;
import com.szampchat.server.permission.data.PermissionScope;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.livekit.auth.LiveKitAuthManager;
import com.szampchat.server.livekit.auth.LiveKitJwtConverter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


@Profile(value = {"development", "production"})
@EnableWebFluxSecurity
@EnableScheduling
@Configuration
@EnableReactiveMethodSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;
    private final LiveKitAuthManager liveKitAuthManager;


    private static final String[] WHITELIST = {
            "/v3/api-docs",
            "/v3/api-docs.yaml",
            "/v3/api-docs/**",
            "/", //swagger ui redirecting path
            "/webjars/swagger-ui/**",
            "/error",
            "/api/file/**",
            "/file/**",
            "/livekit/webhook" //TODO secure it from outside connections somehow
                        // we don't want someone to be able to send fake events
    };

    @Bean
    @Order(1)
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
    @Order(2)
    public SecurityWebFilterChain securityFilterChainLiveKitWebhook(ServerHttpSecurity http) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(liveKitAuthManager);
        authenticationWebFilter.setServerAuthenticationConverter(new LiveKitJwtConverter());
        authenticationWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        return http
                .securityMatcher(ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST ,"/livekit/webhook"))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.configurationSource(apiConfigurationSource()))
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                //TODO validate if body hash matches hash in jwt claim
                .authorizeExchange(auth -> auth.anyExchange().permitAll())
                .build();
    }

    @Bean
    @Order(3)
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers(WHITELIST).permitAll()
                        .anyExchange().authenticated()
                )
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
        conf.setAllowedOrigins(List.of("*"));
        conf.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        conf.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }
}
