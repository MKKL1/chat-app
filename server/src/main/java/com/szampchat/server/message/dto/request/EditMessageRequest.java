package com.szampchat.server.message.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditMessageRequest(
        @NotBlank(message = "Message text cannot be blank")
        @Size(max = 65535, message = "Message text cannot exceed 65535 characters")
        String text
) { }
