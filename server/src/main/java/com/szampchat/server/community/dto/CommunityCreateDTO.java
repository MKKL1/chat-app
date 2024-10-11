package com.szampchat.server.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CommunityCreateDTO(
        @Schema(example = "My community", description = "Name of community")
        String name
) { }
