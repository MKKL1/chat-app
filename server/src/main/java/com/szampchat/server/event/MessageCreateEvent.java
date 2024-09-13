package com.szampchat.server.event;

import com.szampchat.server.message.dto.MessageDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Event fired when message is sent by user
 */
@Getter
@Builder
@ToString
public class MessageCreateEvent implements MessageEvent<MessageDTO> {
    private final String name = "MESSAGE_CREATE_EVENT";
    private final Recipient recipient;
    private final MessageDTO data;
}
