package com.szampchat.server.message.event;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.message.dto.MessageDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Event fired when message is deleted by user
 */
@Getter
@Builder
@ToString
public class MessageDeleteEvent implements InternalEvent<Long> {
    private final String name = "MESSAGE_DELETE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final Long data;
}
