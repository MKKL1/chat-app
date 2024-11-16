package com.szampchat.server.auth.exception;

public class InvalidResourceTypeException extends RuntimeException {
    public InvalidResourceTypeException(String resource) {
        super("Invalid resource type " + resource);
    }
}
