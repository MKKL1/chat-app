package com.szampchat.server.socket;

import com.szampchat.server.auth.CustomJwtAuthenticationConverter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

@Configuration
@EnableRSocketSecurity
@AllArgsConstructor
public class RSocketConfiguration {
    private final CustomJwtAuthenticationManager customJwtAuthenticationManager;

    @Bean
    PayloadSocketAcceptorInterceptor rsocketInterceptor(RSocketSecurity rsocket) {
        rsocket
                .authorizePayload(authorize ->
                        authorize
                                .anyRequest().authenticated()
                                .anyExchange().permitAll()
                )
                .jwt(jwt -> jwt.authenticationManager(customJwtAuthenticationManager));
        return rsocket.build();
    }
}
