package com.szampchat.server.channel.event;

import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.dto.ChannelFullInfoDTO;
import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelUpdateEvent implements InternalEvent<ChannelFullInfoDTO> {
    private final String name = "CHANNEL_UPDATE_EVENT";
    private final EventType type = EventType.MESSAGES;

    private final Recipient recipient;
    private final ChannelFullInfoDTO data;
}
