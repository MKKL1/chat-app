package com.szampchat.server.community.exception;

import com.szampchat.server.shared.exception.BadRequestException;

//@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid invitation")
public class InvalidInvitationException extends BadRequestException {
    public InvalidInvitationException(Long inviteId) {
        super("invalid_invitation", "Invitation " + inviteId + " has expired or is invalid");
    }
}

