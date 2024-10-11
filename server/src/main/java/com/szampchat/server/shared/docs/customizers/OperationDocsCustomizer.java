package com.szampchat.server.shared.docs.customizers;

import com.szampchat.server.shared.docs.DocsProperties;
import com.szampchat.server.shared.docs.OperationDocs;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.HandlerMethod;

@Component
public class OperationDocsCustomizer implements OperationCustomizer {
    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        OperationDocs operationDocs = handlerMethod.getMethodAnnotation(OperationDocs.class);
        if(operationDocs == null) return operation;

        ApiResponses apiResponses = operation.getResponses();

        for (DocsProperties property : operationDocs.value()) {
            switch (property) {
                case DocsProperties.RESPONSE_419 -> apiResponses.addApiResponse("419", new ApiResponse().description("User not created"));
                case DocsProperties.RESPONSE_401 -> apiResponses.addApiResponse("401", new ApiResponse()
                        .description("Incorrect token scope (token not provided?)"));
                case DocsProperties.REQUIRES_MEMBER_PERMISSION ->
                        apiResponses.addApiResponse("403",
                                new ApiResponse().description("User is not a member of community or resource is not available")
                        );
                case DocsProperties.REQUIRES_PARTICIPANT_PERMISSION ->
                        apiResponses.addApiResponse("403",
                                new ApiResponse().description("User is not a member of channel's community or resource is not available")
                        );
                case DocsProperties.REQUIRES_ROLE_ACCESS_PERMISSION ->
                        apiResponses.addApiResponse("403",
                                new ApiResponse().description("User is not a member of role's community or resource is not available")
                        );
                case DocsProperties.REQUIRES_NOT_MEMBER_PERMISSION ->
                        apiResponses.addApiResponse("403",
                                new ApiResponse().description("User is already a member of community or resource is not available")
                        );
                case DocsProperties.REQUIRES_OWNER_PERMISSION ->
                        apiResponses.addApiResponse("403",
                                new ApiResponse().description("User is not an owner of community or resource is not available")
                        );
                case DocsProperties.DOCUMENT_PATH_VARIABLES -> documentPathVariable(operation, handlerMethod);
            }
        }
        return operation;
    }

    private static void documentPathVariable(Operation operation, HandlerMethod handlerMethod) {
        var methodParameters = handlerMethod.getMethod().getParameters();

        for (var methodParameter : methodParameters) {
            // Check if the parameter is annotated with @PathVariable
            PathVariable pathVariableAnnotation = methodParameter.getAnnotation(PathVariable.class);

            if (pathVariableAnnotation != null) {
                // Get the variable name from @PathVariable
                String variableName = pathVariableAnnotation.value().isEmpty()
                        ? methodParameter.getName()
                        : pathVariableAnnotation.value();

                for (Parameter parameter : operation.getParameters()) {
                    if (parameter.getName().equals(variableName)) {
                        customizeParameterBasedOnName(parameter, variableName);
                    }
                }
            }
        }
    }

    private static void customizeParameterBasedOnName(Parameter parameter, String variableName) {
        switch (variableName) {
            case "communityId" -> {
                parameter.description("ID of community")
                        .example("4501678724218880")
                        .required(true);

            }
            case "channelId" -> {
                parameter.description("ID of channel")
                        .example("4501678724218880")
                        .required(true);
            }
            case "userId" -> {
                parameter.description("ID of user")
                        .example("4501678724218880")
                        .required(true);
            }
        }
    }
}
