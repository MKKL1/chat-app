package com.szampchat.server.auth.preauth;

import com.szampchat.server.auth.annotation.HasPermission;
import com.szampchat.server.auth.annotation.ResourceId;
import com.szampchat.server.auth.exception.MissingResourceIdException;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.permission.data.PermissionScope;
import lombok.AllArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@AllArgsConstructor
public class HasPermAuthManagerImpl implements HasPermAuthManager{
    private final ResourcePermissionEvaluator permissionService;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, MethodInvocation methodInvocation) {
        return authentication.flatMap(_ -> {
            Method method = methodInvocation.getMethod();

            HasPermission hasPermission = method.getAnnotation(HasPermission.class);
            if (hasPermission != null) {
                PermissionScope context = hasPermission.scope();
                PermissionFlag[] flags = hasPermission.value();

                Object resourceId = null;
                Parameter[] parameters = method.getParameters();
                Object[] arguments = methodInvocation.getArguments();

                for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i].isAnnotationPresent(ResourceId.class)) {
                        resourceId = arguments[i];
                        break;
                    }
                }

                if (resourceId instanceof Long) {
                    long resource = (Long) resourceId;

                    return permissionService.hasPermission(resource, context, flags)
                            .map(AuthorizationDecision::new);
                } else {
                    return Mono.error(new MissingResourceIdException(method.getName()));
                }
            }

            return Mono.error(new MissingResourceIdException(method.getName()));
        });
    }
}
