package com.szampchat.server.shared.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends ApiException{
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, "bad_request", message);
    }

    public BadRequestException(String errorCode, String message) {
        super(HttpStatus.BAD_REQUEST, errorCode, message);
    }
}
