package com.szampchat.server.shared.exception.handlers;

import com.szampchat.server.shared.exception.dto.ErrorResponse;
import com.szampchat.server.shared.exception.dto.ValidationErrorDTO;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@RestControllerAdvice
@Order(-2)
public class ValidationExceptionHandler {
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse<List<ValidationErrorDTO>>> handleValidationExceptions(final WebExchangeBindException ex, final ServerWebExchange exchange) {
        List<ValidationErrorDTO> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ValidationErrorDTO(fieldError.getField(), fieldError.getDefaultMessage()))
                .toList();

        ErrorResponse<List<ValidationErrorDTO>> errorResponse = ErrorResponse.<List<ValidationErrorDTO>>builder()
                .path(exchange.getRequest().getPath().value())
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation error")
                .type("validation_error")
                .errors(validationErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
