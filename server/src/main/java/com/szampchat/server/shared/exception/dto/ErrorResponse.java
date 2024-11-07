package com.szampchat.server.shared.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ErrorResponse<T> {
    private String path;
    private int status;
    private String error;
    private String type;
    private String message;
    private List<T> errors;
}
