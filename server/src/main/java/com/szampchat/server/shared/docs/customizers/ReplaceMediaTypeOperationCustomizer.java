package com.szampchat.server.shared.docs.customizers;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class ReplaceMediaTypeOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        ApiResponses apiResponses = operation.getResponses();
        apiResponses.forEach((statusCode, apiResponse) -> {
            Content content = apiResponse.getContent();

            if (content != null) {
                if (content.get("*/*") != null) {
                    MediaType schemaMediaType = content.get("*/*");
                    content.remove("*/*");
                    content.addMediaType("application/json", schemaMediaType);
                }
            }

        });

        return operation;
    }
}
