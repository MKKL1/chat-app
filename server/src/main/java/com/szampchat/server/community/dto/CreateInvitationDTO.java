package com.szampchat.server.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateInvitationDTO {

    //This can be time in Long format as a timestamp, giving more flexibility to user
    @NotNull
    @Min(1)
    @Schema(example = "5", description = "Expiration time of invitation")
    private Integer days;
}
