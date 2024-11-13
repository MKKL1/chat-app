package com.szampchat.server.shared.exception.handlers;

import com.szampchat.server.role.exception.InvalidMembersException;
import com.szampchat.server.shared.exception.dto.ErrorResponse;
import com.szampchat.server.shared.exception.dto.ValidationErrorDTO;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

@RestControllerAdvice
@Order(-4)
public class AuthorizationExceptionHandler {

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse<List<ValidationErrorDTO>>> handleValidationExceptions(final AuthorizationDeniedException ex,
                                                                                              final ServerWebExchange exchange) {
        ErrorResponse<List<ValidationErrorDTO>> errorResponse = ErrorResponse.<List<ValidationErrorDTO>>builder()
                .path(exchange.getRequest().getPath().value())
                .status(HttpStatus.FORBIDDEN.value())
                .message(ex.getMessage())
                .type("access_denied")
//                .errors(null)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}
