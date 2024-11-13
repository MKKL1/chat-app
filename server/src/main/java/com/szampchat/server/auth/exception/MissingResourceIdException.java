package com.szampchat.server.auth.exception;

public class MissingResourceIdException extends RuntimeException {
    public MissingResourceIdException(String method) {
        super("Missing ResourceId annotation on one of " + method + " method fields");
    }
}
