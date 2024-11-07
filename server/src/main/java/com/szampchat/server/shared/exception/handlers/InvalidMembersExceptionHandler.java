package com.szampchat.server.shared.exception.handlers;

import com.szampchat.server.role.exception.InvalidMembersException;
import com.szampchat.server.shared.exception.dto.ErrorResponse;
import com.szampchat.server.shared.exception.dto.ValidationErrorDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(-3)
public class InvalidMembersExceptionHandler {

    @ExceptionHandler(InvalidMembersException.class)
    public ResponseEntity<ErrorResponse<List<ValidationErrorDTO>>> handleValidationExceptions(final InvalidMembersException ex,
                                                                                              final ServerWebExchange exchange) {
        List<ValidationErrorDTO> validationErrors = ex.getUsers()
                .stream()
                .map(user -> new ValidationErrorDTO("members/" + user, "Not a member of community"))
                .toList();

        ErrorResponse<List<ValidationErrorDTO>> errorResponse = ErrorResponse.<List<ValidationErrorDTO>>builder()
                .path(exchange.getRequest().getPath().value())
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .type("validation_error_invalid_members")
                .errors(validationErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
