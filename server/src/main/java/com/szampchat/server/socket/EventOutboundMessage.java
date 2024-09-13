package com.szampchat.server.socket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.szampchat.server.event.MessageEvent;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
public class EventOutboundMessage<T> {
    private String name;
    private T data;

    public static <D> EventOutboundMessage<D> fromSendEvent(MessageEvent<D> messageEvent) {
        return new EventOutboundMessage<>(messageEvent.getName(), messageEvent.getData());
    }
}
