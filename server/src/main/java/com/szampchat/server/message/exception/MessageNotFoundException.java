package com.szampchat.server.message.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Message not found")
public class MessageNotFoundException extends RuntimeException{
}
