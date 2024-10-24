package com.szampchat.server.role;

import com.szampchat.server.role.dto.RoleCreateDTO;
import com.szampchat.server.role.dto.RoleDTO;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.service.RoleService;
import com.szampchat.server.shared.docs.OperationDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.szampchat.server.shared.docs.DocsProperties.*;
import static com.szampchat.server.shared.docs.DocsProperties.RESPONSE_401;

@Tag(name = "Role")
@SecurityRequirement(name = "OAuthSecurity")

@AllArgsConstructor
@RestController()
public class RoleController {
    private final RoleService roleService;


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_ROLE_ACCESS_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get role")

    @GetMapping("/communities/{communityId}/roles/{roleId}")
    public Mono<RoleDTO> getRole(@PathVariable Long roleId) {
        return roleService.getRole(roleId);
    }


    //This endpoint is redundant as this data is sent with communities/:communityId/info
//    @ApiResponse(responseCode = "200")
//    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
//
//    @GetMapping("/communities/{communityId}/roles")
//    @PreAuthorize("@communityMemberService.isMember(#communityId, #currentUser.userId)")
//    public Flux<Role> getRolesForCommunity(@PathVariable Long communityId, CurrentUser currentUser) {
//        return roleService.findRolesForCommunity(communityId);
//    }


    //TODO implement
    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_OWNER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Create role TODO")

    @PostMapping("/communities/{communityId}/roles")
    public Mono<RoleDTO> createRole(@PathVariable Long communityId, @RequestBody RoleCreateDTO roleCreateDTO) {
        return roleService.create(roleCreateDTO, communityId);
    }


    //TODO implement
    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Edit role TODO")

    @PatchMapping("/communities/{communityId}/roles/{roleId}")
    public Mono<Role> editRole(@PathVariable Long roleId, @RequestBody RoleCreateDTO roleCreateDTO) {
        return Mono.empty();
    }


    //TODO implement
    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_OWNER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Delete role TODO")

    @DeleteMapping("/communities/{communityId}/roles/{roleId}")
    public Mono<Void> deleteRole(@PathVariable Long roleId) {
        return Mono.empty();
    }
}
