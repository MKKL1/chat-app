package com.szampchat.server.community.event;

import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.user.dto.UserDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommunityUpdateEvent implements InternalEvent<CommunityDTO> {
    private final String name = "COMMUNITY_UPDATE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final CommunityDTO data;
}
