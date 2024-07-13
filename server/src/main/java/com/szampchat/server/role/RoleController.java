package com.szampchat.server.role;

import com.szampchat.server.role.entity.Role;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/communities/{communityId}/roles")
    public Flux<Role> getRolesForCommunity(@PathVariable Long communityId) {
        return roleService.findRolesForCommunity(communityId);
    }
}
