package com.szampchat.server.channel.dto;

import lombok.Data;

@Data
public class ChannelDTO {
    private Long id;
    private String name;
    private Long community;
    private Integer type;
}
