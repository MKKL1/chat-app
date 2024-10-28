package com.szampchat.server.community.event;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.user.dto.UserDTO;
import lombok.Builder;
import lombok.Getter;

//No bans yet
@Getter
@Builder
public class MemberDeleteEvent implements InternalEvent<Long> {
    private final String name = "MEMBER_DELETE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final Long data;
}
