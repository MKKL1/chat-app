package com.szampchat.server.auth;

import com.szampchat.server.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserService userService;

    //Could be replaced by mechanism which injects required value to controller handler method's argument,
    // when prefixed by some kind of annotation (e.g. @WithUser)
    /**
     * Retrieves identifier of user (szampchat/snowflake id) by given subject (auth server/uuid id).
     * @throws UserNotRegisteredException if subject was not found (User was not registered on resource server)
     * @param principal Current principal
     * @return Szampchat's identifier for user
     */
    public Mono<Long> getUserId(Principal principal) {
        return userService.findUserIdBySub(UUID.fromString(principal.getName()))
                .switchIfEmpty(Mono.error(new UserNotRegisteredException()));
    }
}
