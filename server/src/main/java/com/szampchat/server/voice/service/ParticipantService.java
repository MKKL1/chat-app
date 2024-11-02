package com.szampchat.server.voice.service;

import com.szampchat.server.voice.dto.RoomParticipantsDTO;
import com.szampchat.server.livekit.dto.ParticipantDTO;
import com.szampchat.server.voice.mapper.ParticipantMapper;
import com.szampchat.server.voice.mapper.RoomMapper;
import com.szampchat.server.voice.repository.RedisParticipantRepository;
import io.livekit.server.RoomServiceClient;
import livekit.LivekitModels;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class ParticipantService {
    private final RedisParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    public Flux<ParticipantDTO> getRoomParticipants(Long channelId) {
        return participantRepository.findParticipantsByRoomId(channelId.toString());
    }

    public Flux<RoomParticipantsDTO> getRoomParticipants(Collection<Long> channelIds) {
        return Flux.fromIterable(channelIds)
                .flatMap(channelId -> getRoomParticipants(channelId)
                        .map(ParticipantDTO::getIdentity)
                        .flatMap(s -> {
                            try {
                                return Mono.just(participantMapper.participantIdToUserId(s));
                            } catch (IllegalArgumentException e) {
                                log.error("Invalid participant identificator: {}. Participant was skipped", s);
                                return Mono.empty();
                            }
                        })
                        .collect(Collectors.toSet())
                        .map(participants -> new RoomParticipantsDTO(channelId, participants))
                );
    }


}
