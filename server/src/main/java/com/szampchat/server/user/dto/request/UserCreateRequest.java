package com.szampchat.server.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateRequest {
    @NotBlank(message = "User name cannot be blank")
    @Size(max = 32, message = "User name cannot exceed 32 characters")
    private String username;
//    private String imageUrl;
//    private String description;
}
