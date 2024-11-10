package com.szampchat.server.channel.exception;

import com.szampchat.server.shared.exception.AlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ChannelAlreadyExistsException extends AlreadyExistsException {
    public ChannelAlreadyExistsException(Long channelId) {
        super("channel", "Channel " + channelId.toString());
    }

    public ChannelAlreadyExistsException(String channelName) {
        super("channel", "Channel " + channelName);
    }
}
