package com.szampchat.server.voice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantEventDTO {
    private Long channelId;
    private Long userId;
}
