package com.szampchat.server.channel.entity;

import lombok.Getter;

@Getter
public enum ChannelType {
    //TODO remove value, why even use enum if we do it like this
    TEXT_CHANNEL(0),
    VOICE_CHANNEL(1);

    private final short value;

    ChannelType(int value) {
        this.value = (short)value;
    }

}
