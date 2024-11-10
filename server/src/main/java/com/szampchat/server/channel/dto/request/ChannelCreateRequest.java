package com.szampchat.server.channel.dto.request;

import com.szampchat.server.channel.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChannelCreateRequest {
    @NotBlank(message = "Channel name cannot be blank")
    @Size(max = 64, message = "Channel name cannot exceed 64 characters")
    private String name;
    private ChannelType type;
}
