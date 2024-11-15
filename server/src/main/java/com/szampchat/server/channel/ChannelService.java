package com.szampchat.server.channel;

import com.github.fge.jsonpatch.JsonPatch;
import com.szampchat.server.channel.dto.ChannelPatchDiff;
import com.szampchat.server.channel.dto.request.ChannelCreateRequest;
import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.dto.ChannelFullInfoDTO;
import com.szampchat.server.channel.dto.request.ChannelEditRequest;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.channel.event.ChannelCreateEvent;
import com.szampchat.server.channel.event.ChannelDeleteEvent;
import com.szampchat.server.channel.event.ChannelUpdateEvent;
import com.szampchat.server.channel.exception.ChannelAlreadyExistsException;
import com.szampchat.server.channel.exception.ChannelNotFoundException;
import com.szampchat.server.channel.repository.ChannelRepository;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.role.dto.ChannelRoleOverwriteDTO;
import com.szampchat.server.role.dto.ChannelRoleOverwritesDTO;
import com.szampchat.server.role.dto.RoleWithMembersDTO;
import com.szampchat.server.shared.JsonPatchUtil;
import com.szampchat.server.shared.exception.ValidationException;
import com.szampchat.server.voice.service.ParticipantService;
import com.szampchat.server.voice.dto.RoomParticipantsDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import com.szampchat.server.role.service.ChannelRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service("channelService")
@AllArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final CommunityMemberService communityMemberService;
    private final ParticipantService participantService;
    private final ChannelRoleService channelRoleService;
    private final EventSink eventSender;
    private final ModelMapper modelMapper;
    private final Validator validator;

    //TODO cache it (this method will be called on most api operations)
    public Mono<Boolean> isParticipant(Long channelId, Long userId) {
        return getChannel(channelId)
                .flatMap(channel -> communityMemberService.isMember(channel.getCommunityId(), userId));
    }

    public Mono<ChannelDTO> getChannel(Long channelId) {
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

    private Mono<ChannelEditRequest> getChannelFullInfo(Long channelId) {
        return Mono.zip(getChannel(channelId), channelRoleService.getChannelOverwrites(channelId).collectList())
                .map(tuple -> new ChannelEditRequest(modelMapper.map(tuple.getT1(), ChannelEditRequest.ChannelEditDTO.class), tuple.getT2()));
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

    //TODO no check for valid role, throws internal error from r2dbc
    @Transactional
    public Mono<ChannelFullInfoDTO> editChannel(Long channelId, JsonPatch jsonPatch){
        return getChannelFullInfo(channelId)
                .flatMap(oldEditData -> {
                    ChannelEditRequest newEditRequest = patch(oldEditData, jsonPatch);
                    ChannelPatchDiff channelPatchDiff = patchDiff(oldEditData.getOverwrites(), newEditRequest.getOverwrites());
                    return channelRepository.findById(channelId)
                            .switchIfEmpty(Mono.error(new ChannelNotFoundException(channelId)))
                            .flatMap(channel -> {
                                BeanUtils.copyProperties(newEditRequest.getChannel(), channel);

                                Mono<Channel> saveChannel = channelRepository.save(channel);
                                Mono<Void> saveChannelRoles = channelRoleService.add(channelPatchDiff.added(), channelId).then();
                                Mono<Void> updateChannelRoles = channelRoleService.update(channelPatchDiff.updated(), channelId).then();
                                Mono<Void> deleteChannelRoles = channelRoleService.delete(channelPatchDiff.removed(), channelId);

                                return saveChannel
                                        .then(saveChannelRoles)
                                        .then(updateChannelRoles)
                                        .then(deleteChannelRoles)
                                        .then(channelRoleService.getChannelOverwrites(channelId)
                                                .collectList()
                                                .map(list -> new ChannelFullInfoDTO(toDTO(channel), list, null))
                                        );
                            });

                })
                .doOnSuccess(channelFullInfoDTO -> eventSender.publish(
                        ChannelUpdateEvent.builder()
                                .recipient(Recipient.builder()
                                        .context(Recipient.Context.COMMUNITY)
                                        .id(channelFullInfoDTO.getChannel().getCommunityId())
                                        .build())
                                .data(channelFullInfoDTO)
                                .build())
                );
    }

    private ChannelPatchDiff patchDiff(List<ChannelRoleOverwriteDTO> listA, List<ChannelRoleOverwriteDTO> listB) {
        Map<Long, Long> mapA = listA.stream()
                .collect(Collectors.toMap(ChannelRoleOverwriteDTO::getRoleId, ChannelRoleOverwriteDTO::getOverwrites));
        Map<Long, Long> mapB = listB.stream()
                .collect(Collectors.toMap(ChannelRoleOverwriteDTO::getRoleId, ChannelRoleOverwriteDTO::getOverwrites));

        List<ChannelRoleOverwriteDTO> updated = listB.stream()
                .filter(dtoB -> mapA.containsKey(dtoB.getRoleId()) && !Objects.equals(dtoB.getOverwrites(), mapA.get(dtoB.getRoleId())))
                .toList();

        List<ChannelRoleOverwriteDTO> added = listB.stream()
                .filter(dtoB -> !mapA.containsKey(dtoB.getRoleId()))
                .toList();

        List<ChannelRoleOverwriteDTO> removed = listA.stream()
                .filter(dtoA -> !mapB.containsKey(dtoA.getRoleId()))
                .collect(Collectors.toList());

        return new ChannelPatchDiff(updated, added, removed);
    }

    private ChannelEditRequest patch(ChannelEditRequest channelEditRequest, JsonPatch jsonPatch) {
        ChannelEditRequest patch = new JsonPatchUtil<>(ChannelEditRequest.class)
                .patch(channelEditRequest, jsonPatch);

        final BeanPropertyBindingResult errors = new BeanPropertyBindingResult(patch, "channel");
        validator.validate(patch, errors);
        if(errors.hasFieldErrors()) {
            throw new ValidationException(errors.getFieldErrors());
        }
        return patch;
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
        return ChannelDTO.builder()
                .id(channel.getId())
                .name(channel.getName())
                .type(channel.getType())
                .communityId(channel.getCommunityId())
                .build();
    }
}
