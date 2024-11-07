package com.szampchat.server.user.exception;

import com.szampchat.server.shared.exception.AlreadyExistsException;

public class UsernameAlreadyExistsException extends AlreadyExistsException {
    public UsernameAlreadyExistsException(String userName) {
        super("user", "User " + userName);
    }
}
