package com.szampchat.server.voice.service;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.livekit.dto.ParticipantDTO;
import com.szampchat.server.livekit.event.ParticipantJoinedEvent;
import com.szampchat.server.livekit.event.ParticipantLeftEvent;
import com.szampchat.server.voice.dto.ParticipantEventDTO;
import com.szampchat.server.voice.event.ParticipantCreateEvent;
import com.szampchat.server.voice.event.ParticipantDeleteEvent;
import com.szampchat.server.voice.mapper.ParticipantMapper;
import com.szampchat.server.voice.mapper.RoomMapper;
import com.szampchat.server.voice.repository.ParticipantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class ParticipantEventService {
    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;
    private final ChannelService channelService;
    private final RoomMapper roomMapper;
    private final EventSink eventSink;

    public Mono<Void> handleParticipantJoined(ParticipantJoinedEvent event) {
        String roomName = event.getRoom().getName();
        ParticipantDTO participantDTO = participantMapper.toDTO(event.getParticipantInfo());
        return participantRepository.addParticipant(roomName, participantDTO)
                .doOnSuccess(_ -> log.info("Participant {} joined room {}", participantDTO.getIdentity(), roomName))
                //Publish participant create event
                .then(getChannel(roomName)
                        .doOnNext(channelDTO -> eventSink.publish(ParticipantCreateEvent
                                .builder()
                                        .data(new ParticipantEventDTO(channelDTO.getId(), participantMapper.participantIdToUserId(participantDTO.getIdentity())))
                                        .recipient(Recipient.builder()
                                                .id(channelDTO.getCommunityId())
                                                .context(Recipient.Context.COMMUNITY)
                                                .build())
                                .build())))
                .then();
    }

    public Mono<Void> handleParticipantLeft(ParticipantLeftEvent event) {
        String roomName = event.getRoom().getName();
        String participantIdentity = event.getParticipantInfo().getIdentity();
        return participantRepository.removeParticipant(roomName, participantIdentity)
                .doOnSuccess(_ -> log.info("Participant {} left room {}", participantIdentity, roomName))
                //Publish participant delete event
                .then(getChannel(roomName)
                        .doOnNext(channelDTO -> eventSink.publish(ParticipantDeleteEvent
                                .builder()
                                .data(new ParticipantEventDTO(channelDTO.getId(), participantMapper.participantIdToUserId(participantIdentity)))
                                .recipient(Recipient.builder()
                                        .id(channelDTO.getCommunityId())
                                        .context(Recipient.Context.COMMUNITY)
                                        .build())
                                .build())))
                .then();
    }

    private Mono<ChannelDTO> getChannel(String roomName) {
        return Mono.just(roomMapper.roomIdToChannelId(roomName))
                .flatMap(channelService::getChannel);
    }
}
