package com.szampchat.server.user;

import com.szampchat.server.auth.AuthService;
import com.szampchat.server.user.dto.UserCreateDTO;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.user.entity.User;
import com.szampchat.server.user.exception.UserAlreadyExistsException;
import com.szampchat.server.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @GetMapping("/users/me")
    public Mono<UserDTO> getMe() {
        return authService.getAuthenticatedUser()
                .flatMap(authUser -> userService.findUser(authUser.getId()))
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    //TODO save image
    @PostMapping("/users")
    public Mono<Object> createUser(@RequestBody UserCreateDTO userCreateDTO, Principal principal) {
        return userService.findUserIdBySub(UUID.fromString(principal.getName()))
                .flatMap(id -> Mono.error(new UserAlreadyExistsException()))
                .switchIfEmpty(Mono.just(userCreateDTO)
                                .map(userDto -> modelMapper.map(userDto, User.class))
                                        .flatMap(user -> userService.createUser(user, UUID.fromString(principal.getName()))));
    }

    @GetMapping("/users/{userId}")
    public Mono<UserDTO> getUser(@PathVariable Long userId) {
        return userService.findUser(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(user -> modelMapper.map(user, UserDTO.class));
    }
}
