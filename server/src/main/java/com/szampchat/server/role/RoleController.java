package com.szampchat.server.role;

import com.szampchat.server.role.entity.Role;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class RoleController {
    private final RoleService roleService;

    @GetMapping("/roles/{roleId}")
    public Mono<Role> getRole(@PathVariable Long roleId) {
        return roleService.findRole(roleId);
    }

    @GetMapping("/communities/{communityId}/roles")
    public Flux<Role> getRoles(@PathVariable Long communityId) {
        return roleService.findRolesForCommunity(communityId);
    }
}
