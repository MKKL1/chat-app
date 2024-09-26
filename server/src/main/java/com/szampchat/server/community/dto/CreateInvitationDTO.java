package com.szampchat.server.community.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateInvitationDTO {

    //This can be time in Long format, giving more flexibility to user
    @NotNull
    @Min(1)
    private Integer days;
}
