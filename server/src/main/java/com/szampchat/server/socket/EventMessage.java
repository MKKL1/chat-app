package com.szampchat.server.socket;

import com.szampchat.server.event.SendEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventMessage<T> {
    private String name;
    private T data;

    public static <D> EventMessage<D> fromSendEvent(SendEvent<D> sendEvent) {
        return new EventMessage<>(sendEvent.getName(), sendEvent.getData());
    }
}
