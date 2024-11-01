package com.szampchat.server.voice;

import com.szampchat.server.voice.dto.RoomParticipantsDTO;
import com.szampchat.server.voice.livekit.dto.ParticipantDTO;
import com.szampchat.server.voice.repository.ParticipantRepository;
import io.livekit.server.RoomServiceClient;
import livekit.LivekitModels;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;
    private final RoomServiceClient roomServiceClient;

    public Flux<ParticipantDTO> getRoomParticipants(Long channelId) {
        return participantRepository.findParticipantsByRoomId(channelId.toString());
    }

    public Flux<RoomParticipantsDTO> getRoomParticipants(Collection<Long> channelIds) {
        return Flux.fromIterable(channelIds)
                .flatMap(channelId -> getRoomParticipants(channelId)
                        .map(ParticipantDTO::getIdentity)
                        .flatMap(s -> {
                            try {
                                return Mono.just(Long.parseLong(s));
                            } catch (NumberFormatException e) {
                                return Mono.empty();
                            }
                        })
                        .collect(Collectors.toSet())
                        .map(participants -> new RoomParticipantsDTO(channelId, participants))
                );
    }

    ParticipantDTO toDTO(LivekitModels.ParticipantInfo participantInfo) {
        return ParticipantDTO.builder()
                .sid(participantInfo.getSid())
                .name(participantInfo.getName())
                .identity(participantInfo.getIdentity())
                .state(participantInfo.getStateValue())
                .build();
    }
}
