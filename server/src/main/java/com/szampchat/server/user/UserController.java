package com.szampchat.server.user;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.shared.docs.OperationDocs;
import com.szampchat.server.user.dto.UserCreateDTO;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.user.dto.UserDescriptionDTO;
import com.szampchat.server.user.exception.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

import static com.szampchat.server.shared.docs.DocsProperties.*;
import static com.szampchat.server.shared.docs.DocsProperties.RESPONSE_401;

@Tag(name = "User")
@SecurityRequirement(name = "OAuthSecurity")

@AllArgsConstructor
@RestController
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get current user", description = """
            Retrieves current user's data
            """)

    @GetMapping("/users/me")
    public Mono<UserDTO> getMe(CurrentUser currentUser) {
        //TODO fix this, currentUser doesn't hold info
        return Mono.fromCallable(() -> modelMapper.map(currentUser, UserDTO.class));
    }


    //TODO save image
    //This endpoint is secured by oauth resource server without CustomJwtAuthenticationConverter, so we cannot apply CustomUser here
    @ApiResponse(responseCode = "200")
    @OperationDocs({DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Register user", description = """
            Used to create user object in backend server on first login.
            Only route that doesn't throw 419 error.
            To obtain bearer token, check Basics/Authentication page or KeyCloak documentation.
            """)

    @PostMapping("/users")
    public Mono<UserDTO> createUser(@RequestBody UserCreateDTO userCreateDTO, Principal principal) {
        return userService.createUser(userCreateDTO, UUID.fromString(principal.getName()));
    }


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get user")

    @GetMapping("/users/{userId}")
//    @PreAuthorize("//Current user knows specified user")
    public Mono<UserDTO> getUser(@PathVariable Long userId) {
        //TODO move dto mapping to service
        return userService.findUserDTO(userId);
    }


    @Operation(summary = "Edit avatar? TODO")

    @PatchMapping("/users/avatar")
    public Mono<UserDTO> editAvatar(){
        return Mono.empty();
    }


    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Edit description?")

    @PatchMapping("/users/description")
    public Mono<UserDTO> editDescription(UserDescriptionDTO descriptionDTO, CurrentUser user){
        return userService.editDescription(descriptionDTO.description(), user.getUserId());
    }


    //TODO user also needs to be deleted in keycloak
    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Delete user")

    @DeleteMapping("/users")
    public Mono<Void> deleteUser(CurrentUser user){
        return userService.deleteUser(user.getUserId());
    }
}
