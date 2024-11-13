package com.szampchat.server.permission;

import com.szampchat.server.permission.data.PermissionContext;
import com.szampchat.server.permission.data.PermissionFlag;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.authorization.method.MethodInvocationResult;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@AllArgsConstructor
@Component
public class HasPermissionReactiveAuthorizationManager implements ReactiveAuthorizationManager<MethodInvocation> {
    private final ResourcePermissionEvaluator permissionService;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, MethodInvocation methodInvocation) {
        return authentication.flatMap(auth -> {
            // Extract method being called
            Method method = methodInvocation.getMethod();

            // Check if the method is annotated with @HasPermission
            HasPermission hasPermission = method.getAnnotation(HasPermission.class);
            if (hasPermission != null) {
                // Extract fields from the @HasPermission annotation
                PermissionContext context = hasPermission.context();
                PermissionFlag[] flags = hasPermission.value();

                // Extract the resource ID parameter annotated with @ResourceId
                Object resourceId = null;
                Parameter[] parameters = method.getParameters();
                Object[] arguments = methodInvocation.getArguments();

                for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i].isAnnotationPresent(ResourceId.class)) {
                        resourceId = arguments[i];
                        break;
                    }
                }

                // Ensure resourceId is found and valid
                if (resourceId instanceof Long) {
                    long resource = (Long) resourceId;

                    return permissionService.hasPermission(resource, context, flags)
                            .map(AuthorizationDecision::new);
                } else {
                    // Invalid or missing resource ID, deny access
                    return Mono.just(new AuthorizationDecision(false));
                }
            }

            // If @HasPermission is not present, deny access
            return Mono.just(new AuthorizationDecision(false));
        });
    }
}
