package com.szampchat.server.shared.exception.handlers;

import com.szampchat.server.shared.exception.ApiException;
import com.szampchat.server.shared.exception.dto.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public Mono<ResponseEntity<ErrorResponse<Void>>> handleApiException(ApiException ex, ServerWebExchange exchange) {

        ErrorResponse<Void> errorResponse = ErrorResponse.<Void>builder()
                .path(exchange.getRequest().getPath().value())
                .status(ex.getStatus().value())
                .message(ex.getMessage())
                .type(ex.getErrorCode())
                .errors(null)
                .build();

        return Mono.just(new ResponseEntity<>(errorResponse, ex.getStatus()));
    }
}
