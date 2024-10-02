package com.szampchat.server.message.dto;

import lombok.Data;

// TODO add more fields later
@Data
public class MessageCreateDTO{
    String text;
    Long communityId;
    Long respondsToMessage;
    String gifLink;
}
