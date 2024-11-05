package com.szampchat.server.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record JoinRequest(
        @Schema(description = "Valid ID of invitation", example = "29754971884879870")
        Long invitationId
) {}
