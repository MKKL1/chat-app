package com.szampchat.server.auth.preauth;

import com.szampchat.server.auth.exception.InvalidResourceTypeException;
import com.szampchat.server.auth.exception.UnknownCommunityScopeException;
import com.szampchat.server.auth.handler.ResourceAccessHandler;
import com.szampchat.server.permission.PermissionService;
import com.szampchat.server.permission.data.PermissionScope;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.auth.ResourceTypes;
import com.szampchat.server.role.dto.RoleDTO;
import com.szampchat.server.role.service.RoleService;
import com.szampchat.server.shared.CustomPrincipalProvider;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component("auth")
public class ResourcePermissionEvaluator {
    private final Map<String, ResourceTypes> resourceTypeNameMap;
    private final ResourceAccessBeanManager handlerManager;
    private final CustomPrincipalProvider customPrincipalProvider;
    private final PermissionService permissionService;
    private final RoleService roleService;

    public ResourcePermissionEvaluator(ResourceAccessBeanManager handlerManager,
                                       CustomPrincipalProvider customPrincipalProvider,
                                       PermissionService permissionService,
                                       RoleService roleService) {
        resourceTypeNameMap = Arrays.stream(ResourceTypes.values())
                .collect(Collectors.toMap(ResourceTypes::getTypeId, resourceTypes -> resourceTypes));
        this.handlerManager = handlerManager;
        this.customPrincipalProvider = customPrincipalProvider;
        this.permissionService = permissionService;
        this.roleService = roleService;
    }

    public Mono<Boolean> canAccess(long resourceId, String resourceType) {
        //Map string to enum
        return Mono.justOrEmpty(resourceTypeNameMap.get(resourceType))
                .switchIfEmpty(Mono.error(new InvalidResourceTypeException(resourceType)))
                .flatMap(resource -> canAccess(resourceId, resource));
    }

    public Mono<Boolean> canAccess(long resourceId, ResourceTypes resourceType) {
        ResourceAccessHandler handler = handlerManager.get(resourceType);
        return customPrincipalProvider.getPrincipal()
                .flatMap(user -> handler.hasAccess(resourceId, user.getUserId()))
                .onErrorReturn(false);
    }

    public Mono<Boolean> hasPermission(long resourceId, PermissionScope context, PermissionFlag... permissionFlags) {
        switch (context) {
            case COMMUNITY -> {
                return permissionService.hasPermissionInCommunity(resourceId, permissionFlags);
            }
            case CHANNEL -> {
                return permissionService.hasPermissionInChannel(resourceId, permissionFlags);
            }
            case ROLE -> {
                return roleService.getRole(resourceId)
                        .map(RoleDTO::getCommunity)
                        .flatMap(communityId -> permissionService.hasPermissionInCommunity(communityId, permissionFlags));
            }
            default -> {
                return Mono.error(new UnknownCommunityScopeException());
            }
        }
    }
}
