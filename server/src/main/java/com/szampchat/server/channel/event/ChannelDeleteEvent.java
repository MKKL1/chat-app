package com.szampchat.server.channel.event;

import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ChannelDeleteEvent implements InternalEvent<Long> {
    private final String name = "CHANNEL_DELETE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final Long data;
}
