package com.szampchat.server.shared.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.validation.FieldError;

import java.util.List;

@AllArgsConstructor
@Getter
public class ValidationException extends RuntimeException {
    private final List<FieldError> fieldErrors;
}
