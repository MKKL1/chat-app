package com.szampchat.server.permission;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;


public class CustomSecurityExpressionRoot extends SecurityExpressionRoot {

    private final ResourcePermissionConverter resourcePermissionConverter;

    public CustomSecurityExpressionRoot(Authentication authentication, ResourcePermissionConverter resourcePermissionConverter) {
        super(authentication);
        this.resourcePermissionConverter = resourcePermissionConverter;
    }

    public Mono<Boolean> canAccess(long resourceId, String resourceType) {
        return resourcePermissionConverter.canAccess(resourceId, resourceType);
    }

//    public Mono<Boolean> hasPermissions(String... permissions) {
//        return resourcePermissionConverter.hasPermissions(permissions);
//    }
}
