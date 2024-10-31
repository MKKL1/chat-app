package com.szampchat.server.voice.livekit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParticipantDTO {
    private String sid;
    private String identity;
    private String name;
    private int state;
}
