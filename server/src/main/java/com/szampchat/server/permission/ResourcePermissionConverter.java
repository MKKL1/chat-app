package com.szampchat.server.permission;

import com.szampchat.server.permission.data.ResourceTypes;
import com.szampchat.server.shared.CustomPrincipalProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ResourcePermissionConverter {
    private final Map<String, ResourceTypes> resourceTypeNameMap;
    private final ResourceAccessBeanManager handlerManager;
    private final CustomPrincipalProvider customPrincipalProvider;

    public ResourcePermissionConverter(ResourceAccessBeanManager handlerManager, CustomPrincipalProvider customPrincipalProvider) {
        resourceTypeNameMap = Arrays.stream(ResourceTypes.values())
                .collect(Collectors.toMap(ResourceTypes::getTypeId, resourceTypes -> resourceTypes));
        this.handlerManager = handlerManager;
        this.customPrincipalProvider = customPrincipalProvider;
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
}
