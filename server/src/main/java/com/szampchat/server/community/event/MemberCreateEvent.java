package com.szampchat.server.community.event;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.user.dto.UserDTO;
import lombok.Builder;
import lombok.Getter;

/**
 * Fired when user joins community
 */
@Getter
@Builder
public class MemberCreateEvent implements InternalEvent<UserDTO> {
    private final String name = "MEMBER_CREATE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final UserDTO data;
}
