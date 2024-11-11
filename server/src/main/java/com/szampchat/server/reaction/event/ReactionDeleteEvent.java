package com.szampchat.server.reaction.event;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.reaction.dto.ReactionUpdateDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ReactionDeleteEvent implements InternalEvent<ReactionUpdateDTO> {
    private final String name = "REACTION_DELETE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final ReactionUpdateDTO data;
}
