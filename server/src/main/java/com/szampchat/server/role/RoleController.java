package com.szampchat.server.role;

import com.github.fge.jsonpatch.JsonPatch;
import com.szampchat.server.role.dto.RoleCreateRequest;
import com.szampchat.server.role.dto.RoleDTO;
import com.szampchat.server.role.dto.RoleWithMembersDTO;
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

    @GetMapping("/roles/{roleId}")
    public Mono<RoleWithMembersDTO> getRole(@PathVariable Long roleId) {
        return roleService.getRoleWithMembers(roleId);
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


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_OWNER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Create role TODO")

    @PostMapping("/communities/{communityId}/roles")
    public Mono<RoleWithMembersDTO> createRole(@PathVariable Long communityId, @RequestBody RoleCreateRequest roleCreateRequest) {
        return roleService.create(roleCreateRequest, communityId);
    }


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Edit role TODO")

    @PatchMapping("/communities/{communityId}/roles/{roleId}")
    public Mono<RoleWithMembersDTO> editRole(@PathVariable Long roleId, @RequestBody JsonPatch jsonPatch) {
        return roleService.update(roleId, jsonPatch);
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
