package com.szampchat.server.shared.docs.customizers;

import com.szampchat.server.auth.annotation.HasPermission;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PermissionDocsCustomizer implements OperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        HasPermission operationDocs = handlerMethod.getMethodAnnotation(HasPermission.class);
        if(operationDocs == null) return operation;
        List<String> perms = Arrays.stream(operationDocs.value()).map(flag -> flag.getData().name()).toList();
        Map<String, Object> extensions = operation.getExtensions();
        if(extensions == null) {
            extensions = new HashMap<>();
            operation.setExtensions(extensions);
        }
        extensions.put("x-permissions", perms);

        return operation;
    }
}
