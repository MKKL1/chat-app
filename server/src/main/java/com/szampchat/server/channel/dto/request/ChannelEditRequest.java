package com.szampchat.server.channel.dto.request;

import com.szampchat.server.role.dto.ChannelRoleOverwriteDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelEditRequest {
    @Valid
    private ChannelEditDTO channel;
    private List<@Valid ChannelRoleOverwriteDTO> overwrites;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChannelEditDTO {
        @NotBlank(message = "Channel name cannot be blank")
        @Size(max = 32, message = "Channel name cannot exceed 32 characters")
        private String name;
    }
}
