package com.szampchat.server.message.event;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.message.dto.MessageDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Event fired when message is edited by user
 */
@Getter
@Builder
@ToString
public class MessageUpdateEvent implements InternalEvent<MessageDTO> {
    private final String name = "MESSAGE_UPDATE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final MessageDTO data;
}
