package com.szampchat.server.channel;

import lombok.Getter;

@Getter
public enum ChannelType {
    TEXT_CHANNEL(0),
    VOICE_CHANNEL(1);

    private final short value;

    ChannelType(int value) {
        this.value = (short)value;
    }

}
