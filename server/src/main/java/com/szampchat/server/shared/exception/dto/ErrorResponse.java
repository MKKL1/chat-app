package com.szampchat.server.shared.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@Builder
public class ErrorResponse<T> {
    private String path;
    private int status;
    private String type;
    private String message;
    private T errors;
}
