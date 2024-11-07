package com.szampchat.server.shared.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationErrorDTO {
    private String field;
    private String message;
}
