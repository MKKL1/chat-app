package com.szampchat.server.user;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.shared.docs.OperationDocs;
import com.szampchat.server.user.dto.request.UserCreateRequest;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.user.dto.UserDescriptionDTO;
import com.szampchat.server.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.UUID;

import static com.szampchat.server.shared.docs.DocsProperties.*;
import static com.szampchat.server.shared.docs.DocsProperties.RESPONSE_401;

@Tag(name = "User")
@SecurityRequirement(name = "OAuthSecurity")

@Validated
@AllArgsConstructor
@RestController
public class UserController {
    private final UserService userService;

    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get current user", description = """
            Retrieves current user's data
            """)

    @GetMapping("/users/me")
    public Mono<UserDTO> getMe(CurrentUser currentUser) {
        return userService.getUser(currentUser.getUserId());
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
    public Mono<UserDTO> createUser(@RequestBody UserCreateRequest userCreateRequest, Principal principal) {
        return userService.createUser(userCreateRequest, UUID.fromString(principal.getName()));
    }


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get user")

    @GetMapping("/users/{userId}")
    public Mono<UserDTO> getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    //TODO use one endpoint to edit all?
    @Operation(summary = "Edit avatar? TODO")

    @PatchMapping("/users/avatar") //TODO IT SHOULD BE PUT VERY MUCH
    public Mono<UserDTO> editAvatar(@RequestPart("file") FilePart file, CurrentUser user){
        return userService.editAvatar(file, user.getUserId());
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
