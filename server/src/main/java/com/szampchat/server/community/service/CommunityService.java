package com.szampchat.server.community.service;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.channel.dto.ChannelRolesDTO;
import com.szampchat.server.community.dto.CommunityCreateDTO;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.dto.CommunityMemberRolesDTO;
import com.szampchat.server.community.dto.FullCommunityInfoDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.exception.CommunityNotFoundException;
import com.szampchat.server.community.exception.NotOwnerException;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.permission.data.PermissionOverwrites;
import com.szampchat.server.permission.data.Permissions;
import com.szampchat.server.role.service.RoleService;
import com.szampchat.server.community.dto.RoleNoCommunityDTO;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.entity.UserRole;
import com.szampchat.server.role.repository.RoleRepository;
import com.szampchat.server.role.repository.UserRoleRepository;
import com.szampchat.server.shared.CustomPrincipalProvider;
import com.szampchat.server.upload.FileNotFoundException;
import com.szampchat.server.upload.FilePath;
import com.szampchat.server.upload.FileStorageService;
import com.szampchat.server.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.FileSystemException;
import java.util.List;

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

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public Mono<CommunityDTO> findById(Long id) {
        return communityRepository.findById(id)
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()))
                .map(community -> modelMapper.map(community, CommunityDTO.class));
    }

    public Mono<Boolean> isOwner(Long communityId, Long userId) {
        //It can be just findById(communityId) and then check from there
        //TODO use findById
        return communityRepository.isOwnerOfCommunity(communityId, userId).flatMap(
            owner -> owner ? Mono.just(true) : Mono.error(new NotOwnerException())//false?
        );
    }

    public Mono<Boolean> isOwner(Long communityId) {
        return customPrincipalProvider.getPrincipal()
                .flatMap(user -> isOwner(communityId, user.getUserId()))
                .onErrorReturn(false);
    }

    public Mono<FullCommunityInfoDTO> getFullCommunityInfo(Long communityId) {
        //Collect all channels of community with role based permission overwrites
        Mono<List<ChannelRolesDTO>> channelFlux = channelService.getCommunityChannelsWithRoles(communityId).collectList();
        //Collect members with corresponding roles
        Mono<List<CommunityMemberRolesDTO>> memberFlux = communityMemberService.getCommunityMembersWithRoles(communityId).collectList();
        //Collect roles and remove "community" field
        Mono<List<RoleNoCommunityDTO>> roleFlux = roleService.getRolesByCommunity(communityId)
                .map(roleDTO -> modelMapper.map(roleDTO, RoleNoCommunityDTO.class))
                .collectList();

        return findById(communityId)
            .flatMap(community -> Mono.zip(channelFlux, memberFlux, roleFlux)
                .map(data ->  new FullCommunityInfoDTO(community, data.getT1(), data.getT2(), data.getT3()))
            );
    }

    public Flux<Community> getUserCommunities(Long id){
        return communityRepository.userCommunities(id);
    }

    public Mono<Community> save(CommunityCreateDTO communityDTO, FilePart file, Long ownerId) {
        // storing community image
        Mono<String> imageUrlMono = (file != null)
                ? fileStorageService.save(file, FilePath.COMMUNITY)
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
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()))
                .flatMap(savedCommunity -> {
                    Long communityId = savedCommunity.getId();

                    // After creating community its owner also need to be added as its member
                    return userService.findUser(ownerId)
                        .flatMap(savedUser ->
                            communityMemberService.create(communityId, savedUser.getId())
                                .doOnSuccess(row -> log.info(row.toString()))
                                // todo maybe move creating role somewhere else
                                    //TODO or remove default role
                                // also add default role which make sense
                                .then(roleRepository.save(Role.builder()
                                    .name("baseRole")
                                    .permission(new PermissionOverwrites())
                                    .community(communityId).build())
                                ))
                                .flatMap(savedRole -> userRoleRepository.save(
                                    UserRole.builder()
                                        .roleId(savedRole.getId())
                                        .userId(ownerId)
                                        .build()
                                ))
                                .then(Mono.just(savedCommunity)
                    );

                });
        });

    }

    // from chat
    public Mono<CommunityDTO> editCommunity(Long id, Community communityToUpdate, FilePart file){
        return communityRepository.findById(id)
            .flatMap(existingCommunity -> {
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

                if (file != null) {
                    return fileStorageService.save(file, FilePath.COMMUNITY)
                        .flatMap(filepath -> {
                            existingCommunity.setImageUrl(filepath);
                            return communityRepository.save(existingCommunity)
                                    .map(updatedCommunity -> modelMapper.map(updatedCommunity, CommunityDTO.class));
                        });
                } else {
                    return communityRepository.save(existingCommunity)
                            .map(updatedCommunity -> modelMapper.map(updatedCommunity, CommunityDTO.class));
                }
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
}
