package com.szampchat.server.auth;

import com.szampchat.server.CryptoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    public Mono<AuthenticationResponse> register(RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
}
