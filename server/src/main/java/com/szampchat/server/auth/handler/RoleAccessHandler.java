package com.szampchat.server.auth.handler;

import com.szampchat.server.auth.ResourceTypes;
import com.szampchat.server.role.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class RoleAccessHandler implements ResourceAccessHandler {
    private final RoleService roleService;

    @Override
    public ResourceTypes getType() {
        return ResourceTypes.ROLE;
    }

    @Override
    public Mono<Boolean> hasAccess(long resourceId, long userId) {
        return roleService.hasAccessToRoleInfo(resourceId, userId);
    }
}
