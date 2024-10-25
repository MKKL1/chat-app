package com.szampchat.server.role.event;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.role.dto.RoleWithMembersDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class RoleUpdateEvent implements InternalEvent<RoleWithMembersDTO> {
    private final String name = "ROLE_UPDATE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final RoleWithMembersDTO data;
}
