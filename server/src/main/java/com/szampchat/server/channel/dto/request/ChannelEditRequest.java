package com.szampchat.server.channel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChannelEditRequest {

    @NotBlank(message = "Channel name cannot be blank")
    @Size(max = 32, message = "Channel name cannot exceed 32 characters")
    private String name;
}
