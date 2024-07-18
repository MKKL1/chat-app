package com.szampchat.server.auth;

import com.szampchat.server.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class JwtUserDetailsService implements ReactiveUserDetailsService {
    private final UserService userService;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userService.findByEmail(username)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException(username)))
                .map(SecurityUser::new);
    }
}
