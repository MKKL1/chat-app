package com.szampchat.server.role;

import com.github.fge.jsonpatch.JsonPatch;
import com.szampchat.server.auth.annotation.HasPermission;
import com.szampchat.server.auth.annotation.ResourceId;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.permission.data.PermissionScope;
import com.szampchat.server.role.dto.request.RoleCreateRequest;
import com.szampchat.server.role.dto.RoleWithMembersDTO;
import com.szampchat.server.role.service.RoleService;
import com.szampchat.server.shared.docs.JsonPatchSchema;
import com.szampchat.server.shared.docs.OperationDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.szampchat.server.shared.docs.DocsProperties.*;
import static com.szampchat.server.shared.docs.DocsProperties.RESPONSE_401;

@Tag(name = "Role")
@SecurityRequirement(name = "OAuthSecurity")

@Validated
@AllArgsConstructor
@RestController()
public class RoleController {
    private final RoleService roleService;


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_ROLE_ACCESS_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get role")

    @PreAuthorize("@auth.canAccess(#roleId, 'ROLE')")
    @GetMapping("/roles/{roleId}")
    public Mono<RoleWithMembersDTO> getRole(@PathVariable Long roleId) {
        return roleService.getRoleWithMembers(roleId);
    }


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_OWNER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Create role")

    @HasPermission(scope = PermissionScope.COMMUNITY, value = PermissionFlag.ADMINISTRATOR)
    @PreAuthorize("@auth.canAccess(#communityId, 'COMMUNITY')")
    @PostMapping("/communities/{communityId}/roles")
    public Mono<RoleWithMembersDTO> createRole(@ResourceId @PathVariable Long communityId,
                                               @RequestBody RoleCreateRequest roleCreateRequest) {
        return roleService.create(roleCreateRequest, communityId);
    }


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Edit role", description = "Uses json patch to edit RoleWithMembersDTO")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(array = @ArraySchema(schema = @Schema(implementation = JsonPatchSchema.class))))

    @HasPermission(scope = PermissionScope.ROLE, value = PermissionFlag.ADMINISTRATOR)
    @PreAuthorize("@auth.canAccess(#roleId, 'ROLE')")
    @PatchMapping("/roles/{roleId}")
    public Mono<RoleWithMembersDTO> editRole(@ResourceId @PathVariable Long roleId,
                                             @RequestBody JsonPatch jsonPatch) {
        return roleService.update(roleId, jsonPatch);
    }


    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_OWNER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Delete role")

    @HasPermission(scope = PermissionScope.ROLE, value = PermissionFlag.ADMINISTRATOR)
    @PreAuthorize("@auth.canAccess(#roleId, 'ROLE')")
    @DeleteMapping("/roles/{roleId}")
    public Mono<Void> deleteRole(@ResourceId @PathVariable Long roleId) {
        return roleService.delete(roleId);
    }
}
