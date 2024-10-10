package com.szampchat.server.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationResponseDTO {
    @Schema(description = "Invitation link to community", example = "community/4501678724218880/join/29754971884879870")
    private String link;
}
