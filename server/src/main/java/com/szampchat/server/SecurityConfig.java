package com.szampchat.server;

import com.szampchat.server.auth.AuthorizationFunctionFactory;
import com.szampchat.server.auth.AuthorizationManagerFactory;
import com.szampchat.server.auth.CustomJwtAuthenticationConverter;
import com.szampchat.server.auth.UserNotRegisteredException;
import com.szampchat.server.permission.data.PermissionContext;
import com.szampchat.server.permission.data.PermissionFlag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.EnableScheduling;
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
@EnableScheduling
@Configuration
@EnableReactiveMethodSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfig {

    private final CustomJwtAuthenticationConverter customJwtAuthenticationConverter;
    private final AuthorizationManagerFactory authMan;
    private final AuthorizationFunctionFactory authFunc;



    private static final String[] WHITELIST = {
            "/v3/api-docs",
                "/v3/api-docs.yaml",
            "/v3/api-docs/**",
            "/", //swagger ui redirecting path
            "/webjars/swagger-ui/**",
            "/error",
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
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers(WHITELIST).permitAll()
                        //ChannelController
                        .pathMatchers(HttpMethod.POST, "/channels/{communityId}")
                            .access(authMan.create(authFunc.isMember, PermissionContext.COMMUNITY, PermissionFlag.CHANNEL_CREATE))
                        .pathMatchers(HttpMethod.PUT, "/channels/{channelId}")
                            .access(authMan.create(authFunc.isParticipant, PermissionContext.CHANNEL, PermissionFlag.CHANNEL_MODIFY))
                        .pathMatchers(HttpMethod.DELETE, "/channels/{channelId}")
                            .access(authMan.create(authFunc.isParticipant, PermissionContext.CHANNEL, PermissionFlag.CHANNEL_MODIFY))
                        //CommunityController
                        .pathMatchers(HttpMethod.GET, "/communities/{communityId}")
                            .access(authMan.create(authFunc.isMember))
                        .pathMatchers(HttpMethod.GET, "/communities/{communityId}/info")
                            .access(authMan.create(authFunc.isMember))
                        .pathMatchers(HttpMethod.GET, "/communities")
                            .authenticated()
                        .pathMatchers(HttpMethod.POST, "/communities/{communityId}/invite")
                            .access(authMan.create(authFunc.isMember, PermissionContext.COMMUNITY, PermissionFlag.INVITE_CREATE))
                        .pathMatchers(HttpMethod.POST, "/communities/{communityId}/join")
                            .access(authMan.create(authFunc.isNotMember))
                        .pathMatchers(HttpMethod.POST, "/communities/{communityId}/join")
                            .access(authMan.create(authFunc.isNotMember))
                        .pathMatchers(HttpMethod.POST, "/communities")
                            .authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/communities/{communityId}")
                            .access(authMan.create(authFunc.isMember, PermissionContext.COMMUNITY, PermissionFlag.ADMINISTRATOR))
                        .pathMatchers(HttpMethod.DELETE, "/communities/{communityId}")
                            .access(authMan.create(authFunc.isOwner))
                        //MessageController
                        .pathMatchers(HttpMethod.GET, "/channels/{channelId}/messages")
                            .access(authMan.create(authFunc.isParticipant))
                        .pathMatchers(HttpMethod.POST, "/channels/{channelId}/messages")
                            .access(authMan.create(authFunc.isParticipant, PermissionContext.CHANNEL, PermissionFlag.MESSAGE_CREATE))
                        .pathMatchers(HttpMethod.PATCH, "/channels/{channelId}/messages/{messageId}")
                            .access(authMan.create(authFunc.isParticipant)) //TODO user should be able to edit ONLY their message, requires new auth function
                        .pathMatchers(HttpMethod.DELETE, "/channels/{channelId}/messages/{messageId}")
                            .access(authMan.create(authFunc.isParticipant))//TODO delete only their own message
                        //RoleController
                        .pathMatchers(HttpMethod.GET, "/roles/{roleId}")
                            .access(authMan.create(authFunc.hasAccessToRoleInfo))
                        .pathMatchers(HttpMethod.POST, "/communities/{communityId}/roles")
                            .access(authMan.create(authFunc.isMember, PermissionContext.COMMUNITY, PermissionFlag.ADMINISTRATOR))
                        .pathMatchers(HttpMethod.PATCH, "/communities/{communityId}/roles")
                            .access(authMan.create(authFunc.isMember, PermissionContext.COMMUNITY, PermissionFlag.ADMINISTRATOR))
                        .pathMatchers(HttpMethod.DELETE, "/communities/{communityId}/roles")
                            .access(authMan.create(authFunc.isMember, PermissionContext.COMMUNITY, PermissionFlag.ADMINISTRATOR))
                        //UserController
                        .pathMatchers(HttpMethod.GET, "/users/me")
                            .authenticated()
                        .pathMatchers(HttpMethod.GET, "/users/{userId}")
                            .authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/users/avatar")
                            .authenticated()
                        .pathMatchers(HttpMethod.PATCH, "users/description")
                            .authenticated()
                        .pathMatchers(HttpMethod.DELETE, "/users")
                            .authenticated()
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
        conf.setAllowedOrigins(List.of("http://localhost:4200"));
        conf.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        conf.addAllowedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }
}
