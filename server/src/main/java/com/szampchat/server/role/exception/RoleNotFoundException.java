package com.szampchat.server.role.exception;

import com.szampchat.server.shared.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class RoleNotFoundException extends NotFoundException {
    public RoleNotFoundException(Long roleId) {
        super("role", "Role " + roleId);
    }
}
