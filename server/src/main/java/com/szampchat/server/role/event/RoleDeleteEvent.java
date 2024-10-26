package com.szampchat.server.role.event;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class RoleDeleteEvent implements InternalEvent<RoleDeleteEvent.EventPayload> {
    private final String name = "ROLE_DELETE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final EventPayload data;

    public record EventPayload(Long roleId) {}
}
