package com.szampchat.server.role.exception;

import com.szampchat.server.shared.exception.ApiException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collection;

@Getter
public class InvalidMembersException extends RuntimeException {
    private final Collection<Long> users;
    public InvalidMembersException(Collection<Long> users) {
        super("Failed to add members to role.");
        this.users = users;
    }
}
