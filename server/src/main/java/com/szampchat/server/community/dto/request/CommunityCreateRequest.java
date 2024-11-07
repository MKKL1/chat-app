package com.szampchat.server.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommunityCreateRequest(
        @NotBlank(message = "Community name cannot be blank")
        @Size(max = 64, message = "Community name cannot exceed 64 characters")
        @Schema(example = "My community", description = "Name of community")
        String name
) { }
