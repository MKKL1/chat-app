package com.szampchat.server.permission;

public class InvalidResourceTypeException extends RuntimeException {
    public InvalidResourceTypeException(String resource) {
        super("Invalid resource type " + resource);
    }
}
