package com.szampchat.server.channel.exception;

import com.szampchat.server.shared.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ChannelNotFoundException extends NotFoundException {
    public ChannelNotFoundException(Long channelId) {
        super("channel", "Channel " + channelId.toString());
    }
}
