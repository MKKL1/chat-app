package com.szampchat.server.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelDTO {
    private Long id;
    private String name;
    // I changed that to communityId because community breaks data structures on frontend
    private Long communityId;
    private Integer type;
}
