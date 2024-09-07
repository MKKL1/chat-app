package com.szampchat.server.channel;

public enum ChannelType {
    TEXT_CHANNEL(0),
    VOICE_CHANNEL(1);

    private final int value;

    private ChannelType(int value) {
        this.value = value;
    }

    public byte getValue(){
        return (byte) value;
    }
}
