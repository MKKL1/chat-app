package com.szampchat.server.voice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class RoomParticipantsDTO {
    private Long channelId;
    private Set<Long> participants;
}
