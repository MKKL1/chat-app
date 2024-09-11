package com.szampchat.server.event;

import com.szampchat.server.message.dto.MessageDTO;
import com.szampchat.server.message.entity.Message;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class MessageCreateEvent implements SendEvent<MessageDTO> {
    private final String name = "MESSAGE_CREATE_EVENT";
    private final Recipient recipient;
    private final MessageDTO data;
}
