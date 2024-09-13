package com.szampchat.server.user;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.user.dto.UserCreateDTO;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.user.dto.UserDescriptionDTO;
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

    @GetMapping("/users/me")
    public Mono<UserDTO> getMe(CurrentUser currentUser) {
        //TODO fix this, currentUser doesn't hold info
        return Mono.fromCallable(() -> modelMapper.map(currentUser, UserDTO.class));
    }

    //TODO save image
    //This endpoint is secured by oauth resource server without CustomJwtAuthenticationConverter, so we cannot apply CustomUser here
    @PostMapping("/users")
    public Mono<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO, Principal principal) {
        return userService.createUser(userCreateDTO, UUID.fromString(principal.getName()));
    }

    @GetMapping("/users/{userId}")
//    @PreAuthorize("//Current user knows specified user")
    public Mono<UserDTO> getUser(@PathVariable Long userId) {
        return userService.findUser(userId)
                .switchIfEmpty(Mono.error(new UserNotFoundException()))
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    // What if user changes his name in keycloak panel?

    @PatchMapping("/users/avatar")
    public Mono<UserDTO> editAvatar(){
        return Mono.empty();
    }

    @PatchMapping("/users/description")
    public Mono<UserDTO> editDescription(UserDescriptionDTO descriptionDTO, CurrentUser user){
        return userService.editDescription(descriptionDTO.description(), user.getUserId());
    }

    // user also need to be deleted in keycloak
    @DeleteMapping("/users")
    public Mono<Void> deleteUser(CurrentUser user){
        return userService.deleteUser(user.getUserId());
    }
}
