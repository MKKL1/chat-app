package com.szampchat.server.voice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Specified channel was not voice channel")
public class NotVoiceChannelException extends RuntimeException {
}
