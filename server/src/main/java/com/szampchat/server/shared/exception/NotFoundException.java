package com.szampchat.server.shared.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException{
    public NotFoundException(String type, String item) {
        super(HttpStatus.NOT_FOUND, type + "_not_found", item + " not found");
    }
}
