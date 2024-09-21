package com.szampchat.server.message.dto;

import lombok.Data;

// TODO add more fields later
@Data
public class MessageCreateDTO{
    Long id;
    Long channelId;
    Long communityId;
    String text;
}
