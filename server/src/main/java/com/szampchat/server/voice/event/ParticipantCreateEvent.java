package com.szampchat.server.voice.event;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.voice.dto.ParticipantEventDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ParticipantCreateEvent implements InternalEvent<ParticipantEventDTO> {
    private final String name = "PARTICIPANT_CREATE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final ParticipantEventDTO data;
}
