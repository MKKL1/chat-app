package com.szampchat.server.user;

import com.szampchat.server.user.dto.UserCreateDTO;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/users/me")
    public Mono<UserDTO> getMe(Principal principal) {
        return userService.findUserBySub(UUID.fromString(principal.getName()))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

//    @PostMapping("/users")
//    public Mono<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO) {
//
//    }

    @GetMapping("/users/{userId}")
    public Mono<UserDTO> getUser(@PathVariable Long userId) {
        return userService.findUser(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(user -> modelMapper.map(user, UserDTO.class));
    }
}
