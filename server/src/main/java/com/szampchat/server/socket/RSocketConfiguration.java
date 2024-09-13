package com.szampchat.server.socket;

import com.szampchat.server.auth.CustomJwtAuthenticationConverter;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

@Configuration
@EnableRSocketSecurity
@AllArgsConstructor
public class RSocketConfiguration {

    @Bean
    PayloadSocketAcceptorInterceptor rsocketInterceptor(RSocketSecurity rsocket) {
        rsocket
                .authorizePayload(authorize ->
                        authorize
                                .anyRequest().authenticated()
                                .anyExchange().permitAll()
                )
                .jwt(Customizer.withDefaults());
        return rsocket.build();
    }

//    //Not needed probably
//    private JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager() {
//        JwtReactiveAuthenticationManager jwtReactiveAuthenticationManager = new JwtReactiveAuthenticationManager(reactiveJwtDecoder);
//
//        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
//
//        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
//        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
//        authenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
//
//        jwtReactiveAuthenticationManager.setJwtAuthenticationConverter(customJwtAuthenticationConverter);
//        return jwtReactiveAuthenticationManager;
//    }
}
