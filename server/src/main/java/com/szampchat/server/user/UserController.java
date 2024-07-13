package com.szampchat.server.user;

import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.user.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/users/{userId}")
    public Mono<UserDTO> getUser(@PathVariable Long userId) {
        return userService.findUser(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(user -> modelMapper.map(user, UserDTO.class));
    }
}
