package com.szampchat.server.channel;

import com.szampchat.server.channel.dto.ChannelCreateDTO;
import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.dto.ChannelRolesDTO;
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
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import com.szampchat.server.role.service.ChannelRoleService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service("channelService")
@AllArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final CommunityMemberService communityMemberService;
    private ChannelRoleService channelRoleService;

    private final ModelMapper modelMapper;

    private final EventSink eventSender;

    //TODO cache it (this method will be called on most api operations)
    public Mono<Boolean> isParticipant(Long channelId, Long userId) {
        return getChannel(channelId)
//                .doFirst(() -> System.out.println(channelId + " " + userId))
                .flatMap(channel -> communityMemberService.isMember(channel.getCommunityId(), userId));
    }

    @Deprecated
    public Mono<Channel> getChannel(Long channelId) {
        return channelRepository.findById(channelId)
                .switchIfEmpty(Mono.error(new ChannelNotFoundException()));
    }

    public Mono<ChannelDTO> getChannelDTO(Long channelId) {
        return channelRepository.findById(channelId)
                .switchIfEmpty(Mono.error(new ChannelNotFoundException()))
                .map(this::toDTO);
    }


    public Flux<ChannelDTO> getCommunityChannels(Long communityId) {
        return channelRepository.findChannelsByCommunityId(communityId)
                .map(this::toDTO);
    }

    public Flux<ChannelRolesDTO> getCommunityChannelsWithRoles(Long communityId) {
        return getCommunityChannels(communityId)
                .collectList()
                .flatMapMany(channelDTOs -> {
                    List<Long> channelIds = channelDTOs.stream().map(ChannelDTO::getId).collect(Collectors.toList());

                    return channelRoleService.getChannelOverwritesBulk(channelIds)
                            .collectMap(ChannelRoleOverwritesDTO::getChannelId, ChannelRoleOverwritesDTO::getOverwrites)
                            .flatMapMany(overwritesMap ->
                                    Flux.fromIterable(channelDTOs)
                                        .map(channelDTO -> {
                                            List<ChannelRoleOverwriteDTO> overwrites = overwritesMap.getOrDefault(channelDTO.getId(), Collections.emptyList());
                                            return new ChannelRolesDTO(channelDTO, overwrites);
                                        })
                            );
                });
    }

    public Mono<Channel> createChannel(ChannelCreateDTO channel) {
        //TODO check if channel exists in different way
        return channelRepository.doesChannelExist(channel.getName(), channel.getCommunityId())
            .flatMap(existingChannel -> existingChannel ?
                Mono.error(new ChannelAlreadyExistsException()) :
                channelRepository.save(
                    Channel.builder()
                        .name(channel.getName())
                        .communityId(channel.getCommunityId())
                        .type(channel.getType())
                        .build()
                    ).doOnSuccess(savedChannel -> {
                        eventSender.publish(ChannelCreateEvent.builder()
                            // I don't use mapper because I don't want to make custom rules for enum
                            .data(new ChannelDTO(
                                    savedChannel.getId(),
                                    savedChannel.getName(),
                                    savedChannel.getCommunityId(),
                                    savedChannel.getType()
                                )
                            )
                            .recipient(Recipient.builder()
                                    .context(Recipient.Context.COMMUNITY)
                                    .id(savedChannel.getCommunityId())
                                    .build())
                            .build());
                    })
                );
    }

    public Mono<Channel> editChannel(Long id, Channel channel){
        return channelRepository.findById(id)
            .flatMap(existingChannel -> {
               existingChannel = channel;
               return channelRepository.save(existingChannel);
            });
    }

    // issue with constraints
    public Mono<Void> deleteChannel(Long id){
        return channelRepository.findById(id)
            .flatMap(channel -> {
                // this id will be used in eventPublisher
                Long communityId = channel.getCommunityId();
                return channelRepository.deleteById(id)
                    .then(Mono.fromRunnable(() -> {
                        eventSender.publish(
                            ChannelDeleteEvent.builder()
                                .data(id)
                                .recipient(Recipient.builder()
                                    .context(Recipient.Context.COMMUNITY)
                                    .id(communityId)
                                    .build())
                                .build());
                    }));
            });
    }


    public ChannelDTO toDTO(Channel channel) {
        return modelMapper.map(channel, ChannelDTO.class);
    }
}
