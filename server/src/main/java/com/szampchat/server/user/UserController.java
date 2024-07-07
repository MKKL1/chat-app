package com.szampchat.server.user;

import com.szampchat.server.user.entity.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/{userId}")
    public Mono<UserDTO> getUser(@PathVariable Long userId) {
        return userService.findUser(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(user -> modelMapper.map(user, UserDTO.class));
    }
}
