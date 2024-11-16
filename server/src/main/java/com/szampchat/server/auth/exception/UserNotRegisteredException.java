package com.szampchat.server.auth.exception;

import org.springframework.security.core.AuthenticationException;

public class UserNotRegisteredException extends AuthenticationException {
    public UserNotRegisteredException() {
        super("User is not registered.");
    }
}
