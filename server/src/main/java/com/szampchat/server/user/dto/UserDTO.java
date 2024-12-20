package com.szampchat.server.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    @Schema(example = "4501678724218880", description = "ID of user")
    private Long id;
    @Schema(example = "User123", description = "User name")
    private String username;
    @Schema(example = "img/userimage.jpg", description = "Relative path to image (or uuid)")
    private UUID imageUrl;
    @Schema(example = "My description", description = "Short message which describes user")
    private String description;
}
