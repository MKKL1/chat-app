package com.szampchat.server.community.service;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.channel.dto.ChannelFullInfoDTO;
import com.szampchat.server.community.dto.request.CommunityCreateRequest;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.dto.CommunityMemberRolesDTO;
import com.szampchat.server.community.dto.FullCommunityInfoDTO;
import com.szampchat.server.community.dto.request.CommunityEditRequest;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.event.CommunityDeleteEvent;
import com.szampchat.server.community.event.CommunityUpdateEvent;
import com.szampchat.server.community.exception.CommunityNotFoundException;
import com.szampchat.server.community.exception.FailedToSaveCommunityException;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.permission.data.Permissions;
import com.szampchat.server.role.dto.UserRolesDTO;
import com.szampchat.server.role.service.RoleService;
import com.szampchat.server.community.dto.RoleNoCommunityDTO;
import com.szampchat.server.role.service.UserRoleService;
import com.szampchat.server.upload.FilePathType;
import com.szampchat.server.upload.FileStorageService;
import com.szampchat.server.upload.dto.FileDTO;
import com.szampchat.server.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserService userService;
    private final CommunityMemberService communityMemberService;
    private final ChannelService channelService;
    private final RoleService roleService;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;
    private final UserRoleService userRoleService;
    private final EventSink eventSink;

    public Mono<CommunityDTO> getById(Long id) {
        return communityRepository.findById(id)
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()))
                .map(community -> modelMapper.map(community, CommunityDTO.class));
    }

    public Mono<Boolean> isOwner(Long communityId, Long userId) {
        return communityRepository.isOwnerOfCommunity(communityId, userId);
    }

    public Mono<FullCommunityInfoDTO> getFullCommunityInfo(Long communityId) {
        //Collect all channels of community with role based permission overwrites
        Mono<List<ChannelFullInfoDTO>> channelFlux = channelService.getCommunityChannelsFullInfo(communityId).collectList();
        //Collect members with corresponding roles
        Mono<List<CommunityMemberRolesDTO>> memberFlux = getCommunityMembersWithRoles(communityId).collectList();
        //Collect roles and remove "community" field
        Mono<List<RoleNoCommunityDTO>> roleFlux = roleService.getRolesByCommunity(communityId)
                .map(roleDTO -> modelMapper.map(roleDTO, RoleNoCommunityDTO.class))
                .collectList();

        return getById(communityId)
            .flatMap(community -> Mono.zip(channelFlux, memberFlux, roleFlux)
                .map(data ->  new FullCommunityInfoDTO(community, data.getT1(), data.getT2(), data.getT3()))
            );
    }

    private Flux<CommunityMemberRolesDTO> getCommunityMembersWithRoles(Long communityId) {
        return communityMemberService.getByCommunityId(communityId)
                .map(CommunityMember::getUserId)
                .collectList()
                .flatMapMany(userIds ->
                        userRoleService.getMemberRoleIdsBulk(userIds, communityId)
                                .collectMap(UserRolesDTO::getUserId, Function.identity())
                                .flatMapMany(userRolesMap ->
                                        userService.findUsers(userIds)
                                                .map(userDto -> {
                                                    UserRolesDTO userRolesDTO = userRolesMap.get(userDto.getId());
                                                    return CommunityMemberRolesDTO.builder()
                                                            .user(userDto)
                                                            .roles(userRolesDTO != null ? userRolesDTO.getRoleIds() : Collections.emptySet())
                                                            .build();
                                                })
                                )
                );
    }

    public Flux<CommunityDTO> getUserCommunities(Long id){
        return communityRepository.userCommunities(id)
                .map(this::toDTO);
    }

    @Transactional
    public Mono<Community> save(CommunityCreateRequest communityDTO, FilePart file, Long ownerId) {
        // storing community image
        Mono<UUID> imageUploadMono = Mono.justOrEmpty(file)
                .flatMap(f -> fileStorageService.upload(f, FilePathType.COMMUNITY))
                .map(FileDTO::getId);

        // creating file to save in database
        return imageUploadMono
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .map(optionalImageId -> Community.builder()
                        .name(communityDTO.name())
                        .ownerId(ownerId)
                        .imageUrl(optionalImageId.orElse(null))
                        .basePermissions(new Permissions())
                        .build())
                .flatMap(community -> communityRepository.save(community)
                        //Since I don't see when saving a new community could fail (data is validated on controller),
                        // I am going to leave it as internal server error
                    .switchIfEmpty(Mono.error(new FailedToSaveCommunityException()))
                    .flatMap(savedCommunity -> {
                        Long communityId = savedCommunity.getId();

                        // After creating community its owner also need to be added as its member
                        return communityMemberService.create(communityId, ownerId).thenReturn(savedCommunity);
                        //TODO maybe add default text channel?
                    })
                );

    }

    // from chat
    public Mono<CommunityDTO> editCommunity(Long id, CommunityEditRequest request, FilePart file) {
        return communityRepository.findById(id)
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()))
                .flatMap(existingCommunity ->
                        fileStorageService.replace(file, FilePathType.COMMUNITY, existingCommunity.getImageUrl())
                        .doOnNext(newFileDTO -> existingCommunity.setImageUrl(newFileDTO.getId()))
                        .thenReturn(existingCommunity)
                )
                .flatMap(existingCommunity -> {
                    if (request.getName() != null)
                        existingCommunity.setName(request.getName());

                    if (request.getBasePermissions() != null)
                        existingCommunity.setBasePermissions(new Permissions(request.getBasePermissions()));

                    return communityRepository.save(existingCommunity)
                            .map(updatedCommunity -> modelMapper.map(updatedCommunity, CommunityDTO.class))
                            .doOnNext(communityDTO -> eventSink.publish(
                                    CommunityUpdateEvent.builder()
                                            .recipient(Recipient.builder()
                                                    .context(Recipient.Context.COMMUNITY)
                                                    .id(communityDTO.getId())
                                                    .build())
                                            .data(communityDTO)
                                            .build())
                            );
                });
    }

    public Mono<Void> delete(Long id) {
        return getById(id)
                .flatMap(communityDTO -> {
                            Mono<Void> deleteImageMono = communityDTO.getImageUrl() == null ?
                                    Mono.empty() :
                                    fileStorageService.delete(communityDTO.getImageUrl());

                            return deleteImageMono.then(communityRepository.deleteById(id));
                }).doOnSuccess(_ -> eventSink.publish(
                        CommunityDeleteEvent.builder()
                                .data(id)
                                .recipient(Recipient.builder()
                                        .context(Recipient.Context.COMMUNITY)
                                        .id(id)
                                        .build())
                                .build())
                );
    }

    private CommunityDTO toDTO(Community community) {
        return modelMapper.map(community, CommunityDTO.class);
    }
}
