package com.szampchat.server.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "User already exists")
public class UserAlreadyExistsException extends Exception{
}
