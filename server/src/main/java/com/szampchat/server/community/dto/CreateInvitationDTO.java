package com.szampchat.server.community.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateInvitationDTO {

    @NotNull
    @Min(1)
    private Integer days;
}
