package com.szampchat.server.community.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "No owner permissions")
public class NotOwnerException extends RuntimeException{
}
