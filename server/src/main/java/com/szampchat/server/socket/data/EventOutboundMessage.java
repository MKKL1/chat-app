package com.szampchat.server.socket.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szampchat.server.event.data.InternalEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class EventOutboundMessage<T> {
    private String name;
    private T data;

    public static <D> EventOutboundMessage<D> fromSendEvent(InternalEvent<D> internalEvent) {
        return new EventOutboundMessage<>(internalEvent.getName(), internalEvent.getData());
    }
}
