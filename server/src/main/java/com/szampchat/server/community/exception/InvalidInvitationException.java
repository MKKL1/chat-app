package com.szampchat.server.community.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid invitation")
public class InvalidInvitationException extends RuntimeException{
}

