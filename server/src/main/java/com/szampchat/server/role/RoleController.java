package com.szampchat.server.role;

import com.szampchat.server.role.dto.RoleCreateDTO;
import com.szampchat.server.role.entity.Role;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class RoleController {
    private final RoleService roleService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Role by given ID was not found", content = @Content),
    })
    @GetMapping("/roles/{roleId}")
    public Mono<Role> getRole(@PathVariable Long roleId) {
        return roleService.findRole(roleId);
    }

    //TODO present roles in community GET endpoint
    @GetMapping("/communities/{communityId}/roles")
    public Flux<Role> getRolesForCommunity(@PathVariable Long communityId) {
        return roleService.findRolesForCommunity(communityId);
    }

    @PostMapping("/roles")
    public Mono<Role> createRole(@RequestBody RoleCreateDTO roleCreateDTO) {
        return Mono.empty();
    }

    @PatchMapping("/roles/{roleId}")
    public Mono<Role> editRole(@PathVariable Long roleId, @RequestBody RoleCreateDTO roleCreateDTO) {
        return Mono.empty();
    }

    @DeleteMapping("/roles/{roleId}")
    public Mono<Void> deleteRole(@PathVariable Long roleId) {
        return Mono.empty();
    }
}
