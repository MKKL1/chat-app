package com.szampchat.server.auth;

import com.szampchat.server.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

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
     * @return Szampchat's identifier for user
     */
    public Mono<AuthUser> getAuthenticatedUser() {
        return ReactiveSecurityContextHolder.getContext()
                .flatMap(securityContext -> Mono.justOrEmpty(securityContext.getAuthentication()))
//                .switchIfEmpty(Mono.error())
                .flatMap(authentication -> userService.findUserIdBySub(UUID.fromString(authentication.getName())))
                .switchIfEmpty(Mono.error(new UserNotRegisteredException()))
                .map(AuthUser::new);
    }
}
