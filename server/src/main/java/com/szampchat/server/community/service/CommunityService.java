package com.szampchat.server.community.service;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.channel.dto.ChannelFullInfoDTO;
import com.szampchat.server.community.dto.request.CommunityCreateRequest;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.dto.CommunityMemberRolesDTO;
import com.szampchat.server.community.dto.FullCommunityInfoDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
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
import com.szampchat.server.shared.CustomPrincipalProvider;
import com.szampchat.server.upload.FilePathType;
import com.szampchat.server.upload.FileStorageService;
import com.szampchat.server.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.FileSystemException;
import java.util.Collections;
import java.util.List;
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
    private final CustomPrincipalProvider customPrincipalProvider;
    private final ModelMapper modelMapper;
    private final FileStorageService fileStorageService;
    private final UserRoleService userRoleService;
    private final EventSink eventSink;

    public Mono<CommunityDTO> findById(Long id) {
        return communityRepository.findById(id)
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()))
                .map(community -> modelMapper.map(community, CommunityDTO.class));
    }

    public Mono<Boolean> isOwner(Long communityId, Long userId) {
        return communityRepository.isOwnerOfCommunity(communityId, userId);
    }

    public Mono<Boolean> isOwner(Long communityId) {
        return customPrincipalProvider.getPrincipal()
                .flatMap(user -> isOwner(communityId, user.getUserId()))
                .onErrorReturn(false);
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

        return findById(communityId)
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
        Mono<String> imageUrlMono = (file != null)
                ? fileStorageService.save(file, FilePathType.COMMUNITY)
                : Mono.just(null);

        // creating file to save in database
        return imageUrlMono.flatMap(imageUrl -> {
            Community community = Community.builder()
                    .name(communityDTO.name())
                    .ownerId(ownerId)
                    .imageUrl(imageUrl)
                    .basePermissions(new Permissions())
                    .build();

            return communityRepository.save(community)
                    //Since I don't see when saving a new community could fail (data is validated on controller),
                    // I am going to leave it as internal server error
                .switchIfEmpty(Mono.error(new FailedToSaveCommunityException()))
                .flatMap(savedCommunity -> {
                    Long communityId = savedCommunity.getId();

                    // After creating community its owner also need to be added as its member
                    return userService.findUserDTO(ownerId)
                        .flatMap(savedUser ->
                            communityMemberService.create(communityId, savedUser.getId())
                                .doOnSuccess(row -> log.info(row.toString()))
                                .then(Mono.just(savedCommunity))
                                //TODO maybe add default text channel?
                    );
                });
        });

    }

    // from chat
    public Mono<CommunityDTO> editCommunity(Long id, Community communityToUpdate, FilePart file) {
        return communityRepository.findById(id)
            .flatMap(existingCommunity -> {
                //TODO simplify
                if (existingCommunity.getImageUrl() != null) {
                    try {
                        return fileStorageService.delete(existingCommunity.getImageUrl())
                            .onErrorMap(e -> new FileNotFoundException("Error during deleting file: " + e.getMessage()))
                            .then(Mono.just(existingCommunity));
                    } catch (FileSystemException e) {
                        return Mono.error(new FileSystemException("Cannot delete file"));
                    }
                } else {
                    return Mono.just(existingCommunity);
                }
            })
            .flatMap(existingCommunity -> {
                BeanUtils.copyProperties(communityToUpdate, existingCommunity, "id", "imageUrl");

                //Save image if changed
                return Mono.justOrEmpty(file)
                        .flatMap(filePart -> fileStorageService.save(filePart, FilePathType.COMMUNITY))
                        .doOnNext(existingCommunity::setImageUrl)
                        //Save community
                        .then(communityRepository.save(existingCommunity))
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
        return communityRepository.findById(id)
            .flatMap(existingCommunity -> {
                if (existingCommunity.getImageUrl() != null) {
                    try {
                        return fileStorageService.delete(existingCommunity.getImageUrl())
                            .then(communityRepository.deleteById(id));
                    } catch (FileSystemException e) {
                        return Mono.error(e.getCause());
                    }
                } else {
                    return communityRepository.deleteById(id)
                        .doOnSuccess(_ -> log.info("Deleted community by id: {}", id));
                }
            });
    }

    CommunityDTO toDTO(Community community) {
        return modelMapper.map(community, CommunityDTO.class);
    }
}
