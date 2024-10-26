package com.szampchat.server.channel.dto;

import lombok.AllArgsConstructor;
import com.szampchat.server.channel.ChannelType;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelDTO {
    private Long id;
    private String name;
    private Long communityId;
    private ChannelType type;
}
