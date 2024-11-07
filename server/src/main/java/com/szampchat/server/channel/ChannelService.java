package com.szampchat.server.channel;

import com.szampchat.server.channel.dto.request.ChannelCreateRequest;
import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.dto.ChannelFullInfoDTO;
import com.szampchat.server.channel.dto.request.ChannelEditRequest;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.channel.event.ChannelCreateEvent;
import com.szampchat.server.channel.event.ChannelDeleteEvent;
import com.szampchat.server.channel.exception.ChannelAlreadyExistsException;
import com.szampchat.server.channel.exception.ChannelNotFoundException;
import com.szampchat.server.channel.repository.ChannelRepository;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.role.dto.ChannelRoleOverwriteDTO;
import com.szampchat.server.role.dto.ChannelRoleOverwritesDTO;
import com.szampchat.server.voice.service.ParticipantService;
import com.szampchat.server.voice.dto.RoomParticipantsDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import com.szampchat.server.role.service.ChannelRoleService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("channelService")
@AllArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final CommunityMemberService communityMemberService;
    private final ParticipantService participantService;
    private final ChannelRoleService channelRoleService;
    private final ModelMapper modelMapper;
    private final EventSink eventSender;

    //TODO cache it (this method will be called on most api operations)
    public Mono<Boolean> isParticipant(Long channelId, Long userId) {
        return getChannel(channelId)
                .flatMap(channel -> communityMemberService.isMember(channel.getCommunityId(), userId));
    }

    @Deprecated
    public Mono<Channel> getChannel(Long channelId) {
        return channelRepository.findById(channelId)
                .switchIfEmpty(Mono.error(new ChannelNotFoundException(channelId)));
    }

    public Mono<ChannelDTO> getChannelDTO(Long channelId) {
        return channelRepository.findById(channelId)
                .switchIfEmpty(Mono.error(new ChannelNotFoundException(channelId)))
                .map(this::toDTO);
    }


    public Flux<ChannelDTO> getCommunityChannels(Long communityId) {
        return channelRepository.findChannelsByCommunityId(communityId)
                .map(this::toDTO);
    }

    public Flux<ChannelFullInfoDTO> getCommunityChannelsFullInfo(Long communityId) {
        return getCommunityChannels(communityId)
                .collectList()
                .flatMapMany(channelDTOs -> {
                    List<Long> channelIds = channelDTOs.stream()
                            .map(ChannelDTO::getId)
                            .toList();

                    List<Long> voiceChannelIds = channelDTOs.stream()
                            .filter(channelDTO -> channelDTO.getType() == ChannelType.VOICE_CHANNEL)
                            .map(ChannelDTO::getId)
                            .toList();

                    Mono<Map<Long, List<ChannelRoleOverwriteDTO>>> overwritesMapMono = channelRoleService.getChannelOverwritesBulk(channelIds)
                            .collectMap(ChannelRoleOverwritesDTO::getChannelId, ChannelRoleOverwritesDTO::getOverwrites);

                    Mono<Map<Long, Set<Long>>> participantsMono = participantService.getRoomParticipants(voiceChannelIds)
                            .collectMap(RoomParticipantsDTO::getChannelId, RoomParticipantsDTO::getParticipants);

                    return Mono.zip(overwritesMapMono, participantsMono)
                            .flatMapMany(tuple2 -> {
                                Map<Long, List<ChannelRoleOverwriteDTO>> overwritesMap = tuple2.getT1();
                                Map<Long, Set<Long>> participantsMap = tuple2.getT2();

                                return Flux.fromIterable(channelDTOs)
                                        .map(channelDTO -> {
                                            List<ChannelRoleOverwriteDTO> overwrites = overwritesMap.getOrDefault(channelDTO.getId(), Collections.emptyList());
                                            Set<Long> participants = participantsMap.get(channelDTO.getId());
                                            return new ChannelFullInfoDTO(channelDTO, overwrites, participants);
                                        });
                            });
                });
    }

    public Mono<ChannelDTO> createChannel(ChannelCreateRequest channelCreateRequest, Long communityId) {
        return channelRepository.existsByNameAndCommunityId(channelCreateRequest.getName(), communityId)
                .flatMap(channelExists -> {
                    if (channelExists) {
                        return Mono.error(new ChannelAlreadyExistsException(channelCreateRequest.getName()));
                    }

                    Channel newChannel = Channel.builder()
                            .name(channelCreateRequest.getName())
                            .communityId(communityId)
                            .type(channelCreateRequest.getType())
                            .build();

                    return channelRepository.save(newChannel);
                })
                .map(this::toDTO)
                .doOnSuccess(savedChannel -> eventSender.publish(ChannelCreateEvent.builder()
                        .data(savedChannel)
                        .recipient(Recipient.builder()
                                .context(Recipient.Context.COMMUNITY)
                                .id(savedChannel.getCommunityId())
                                .build())
                        .build())
                );
    }

    public Mono<ChannelDTO> editChannel(Long channelId, ChannelEditRequest channelEditRequest){
        return Mono.error(new ChannelNotFoundException(channelId));
//        return channelRepository.findById(channelId)
//                .switchIfEmpty(Mono.error(new ChannelNotFoundException()))
//                .flatMap(channel -> {
//                    channel.setName(channelEditRequest.getName());
//                    return channelRepository.save(channel);
//                })
//                .map(this::toDTO);
    }

    // issue with constraints
    public Mono<Void> deleteChannel(Long id){
        return channelRepository.findById(id)
            .flatMap(channel -> channelRepository.deleteById(id)
                    .doOnSuccess(_ -> eventSender.publish(
                            ChannelDeleteEvent.builder()
                                    .data(id)
                                    .recipient(Recipient.builder()
                                            .context(Recipient.Context.COMMUNITY)
                                            .id(channel.getCommunityId())
                                            .build())
                                    .build())
                    )
            );
    }


    public ChannelDTO toDTO(Channel channel) {
        return modelMapper.map(channel, ChannelDTO.class);
    }
}
