package com.szampchat.server.role.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidMembersException extends RuntimeException{
    public InvalidMembersException(Collection<Long> users) {
        super("Failed to add members to role. Invalid members: [" + String.join(",", users.stream().map(Object::toString).toList()) + "]");
    }
}
