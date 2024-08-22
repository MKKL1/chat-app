package com.szampchat.server.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserNotRegisteredException extends AuthenticationException {
    public UserNotRegisteredException() {
        super("User is not registered.");
    }
}
