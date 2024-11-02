package com.szampchat.server.voice.mapper;

import com.szampchat.server.livekit.dto.ParticipantDTO;
import com.szampchat.server.voice.exception.InvalidParticipantException;
import livekit.LivekitModels;
import org.springframework.stereotype.Component;

@Component
public class ParticipantMapper {

    public String userIdToParticipantId(Long userId) {
        return userId.toString();
    }

    public Long participantIdToUserId(String participantId) {
        try {
            return Long.parseLong(participantId);
        } catch (NumberFormatException e) {
            throw new InvalidParticipantException(participantId);
        }
    }

    public ParticipantDTO toDTO(LivekitModels.ParticipantInfo participantInfo) {
        return ParticipantDTO.builder()
                .sid(participantInfo.getSid())
                .name(participantInfo.getName())
                .identity(participantInfo.getIdentity())
                .state(participantInfo.getStateValue())
                .build();
    }
}
