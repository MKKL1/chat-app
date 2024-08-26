package com.szampchat.server.channel.dto;

import com.szampchat.server.channel.ChannelType;
import lombok.Data;

@Data
public class ChannelCreateDTO {
    private Long communityId;
    private String name;
    private ChannelType type;
}
