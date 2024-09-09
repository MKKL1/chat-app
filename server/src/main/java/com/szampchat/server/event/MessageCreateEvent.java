package com.szampchat.server.event;

import com.szampchat.server.message.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class MessageCreateEvent extends AbstractEvent {
    //TODO not sure if it should be entity or DTO object
    private final Message message;

    public MessageCreateEvent(Message message) {
        super((byte) 1);
        this.message = message;
    }
}
