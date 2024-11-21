package com.szampchat.server.community.event;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommunityDeleteEvent implements InternalEvent<Long> {
    private final String name = "COMMUNITY_DELETE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final Long data;
}
