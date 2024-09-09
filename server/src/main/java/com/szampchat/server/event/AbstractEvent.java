package com.szampchat.server.event;

public abstract class AbstractEvent {
    protected final Byte id;

    protected AbstractEvent(Byte id) {
        this.id = id;
    }
}
