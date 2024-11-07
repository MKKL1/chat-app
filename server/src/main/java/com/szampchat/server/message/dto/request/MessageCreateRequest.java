package com.szampchat.server.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

// TODO add more fields later
@Data
public class MessageCreateRequest {

    @NotBlank(message = "Message text cannot be blank")
    @Size(max = 65535, message = "Message text cannot exceed 65535 characters")
    private String text;
    @NotNull(message = "Community ID is required")
    private Long communityId;
    private Long respondsToMessage;
    @Size(max = 255, message = "GIF link cannot exceed 2048 characters")
    @Pattern(
            regexp = "^(http|https)://.*\\.(gif|png|jpg|jpeg|webp)$",
            message = "Invalid URL format for the GIF link"
    )
    private String gifLink;
}
