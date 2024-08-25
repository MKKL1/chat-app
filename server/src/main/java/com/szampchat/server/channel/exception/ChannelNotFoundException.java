package com.szampchat.server.channel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Channel not found")
public class ChannelNotFoundException extends RuntimeException{
}
