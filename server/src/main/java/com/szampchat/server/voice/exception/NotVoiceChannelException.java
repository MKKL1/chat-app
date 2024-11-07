package com.szampchat.server.voice.exception;

import com.szampchat.server.shared.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class NotVoiceChannelException extends BadRequestException {
    public NotVoiceChannelException(Long channelid) {
        super("not_voice_channel", "Channel " + channelid + " is not a voice channel");
    }
}
