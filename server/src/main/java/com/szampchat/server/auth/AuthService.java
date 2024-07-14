package com.szampchat.server.auth;

import com.szampchat.server.CryptoService;
import com.szampchat.server.user.UserService;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AuthService {
    private final CryptoService cryptoService;
    private final UserService userService;

    public Mono<AuthenticationResponse> register(RegisterRequest registerRequest) {
        return userService.createUser(User.builder()
                        .name(registerRequest.getUsername())
                        .email(registerRequest.getEmail())
                        .password(cryptoService.encodePassword(registerRequest.getPassword()))
                .build())
                .map(SecurityUser::new)
                .map(user -> AuthenticationResponse.builder()
                        .accessToken(cryptoService.generateToken(user))
                        .refreshToken("TODO")
                        .build());
    }
}
