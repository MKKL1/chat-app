package com.szampchat.server.user.event;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.user.dto.UserDTO;
import lombok.Builder;
import lombok.Getter;

//Not used for now
@Getter
@Builder
public class UserUpdateEvent implements InternalEvent<UserDTO> {
    private final String name = "USER_UPDATE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final UserDTO data;
}
