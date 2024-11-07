package com.szampchat.server.user.exception;

import com.szampchat.server.shared.exception.AlreadyExistsException;

import java.util.UUID;

public class KeycloakUserAlreadyExistsException extends AlreadyExistsException {
    public KeycloakUserAlreadyExistsException(UUID userId) {
        super("keycloak_user", "Keycloak user " + userId);
    }
}
