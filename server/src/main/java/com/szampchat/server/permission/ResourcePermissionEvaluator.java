package com.szampchat.server.permission;

import com.szampchat.server.permission.data.PermissionContext;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.permission.data.ResourceTypes;
import com.szampchat.server.shared.CustomPrincipalProvider;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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

    public ResourcePermissionEvaluator(ResourceAccessBeanManager handlerManager, CustomPrincipalProvider customPrincipalProvider, PermissionService permissionService) {
        resourceTypeNameMap = Arrays.stream(ResourceTypes.values())
                .collect(Collectors.toMap(ResourceTypes::getTypeId, resourceTypes -> resourceTypes));
        this.handlerManager = handlerManager;
        this.customPrincipalProvider = customPrincipalProvider;
        this.permissionService = permissionService;
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

    public Mono<Boolean> hasPermission(long resourceId, PermissionContext context, PermissionFlag... permissionFlags) {
        switch (context) {
            case COMMUNITY -> {
                return permissionService.hasPermissionInCommunity(resourceId, permissionFlags);
            }
            case CHANNEL -> {
                return permissionService.hasPermissionInChannel(resourceId, permissionFlags);
            }
        }
        return Mono.just(false);
    }
}
