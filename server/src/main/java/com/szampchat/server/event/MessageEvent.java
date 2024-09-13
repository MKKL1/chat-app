package com.szampchat.server.event;

import com.szampchat.server.socket.EventOutboundMessage;

/**
 * Internal form of event before it's sent to event channel given by {@link Recipient}.
 * @param <T> Event data.
 */
public interface MessageEvent<T> {
    /**
     * @return Unique identificator of event, e.g. "MESSAGE_CREATE_EVENT"
     */
    String getName();

    /**
     * @return Receiver of event
     */
    Recipient getRecipient();

    /**
     * @return Data that will be directly mapped to {@link EventOutboundMessage#getData()} (Receiver or client will receive this data)
     */
    T getData();
}
