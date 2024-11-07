package com.szampchat.server.shared.exception;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends ApiException {
    protected AlreadyExistsException(String type, String item) {
        super(HttpStatus.CONFLICT, type + "_conflict", item + " already exists");
    }
}
