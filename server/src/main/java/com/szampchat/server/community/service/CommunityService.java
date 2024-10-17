package com.szampchat.server.community.service;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.community.dto.CommunityCreateDTO;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.dto.CommunityMemberRolesDTO;
import com.szampchat.server.community.dto.FullCommunityInfoDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.exception.CommunityNotFoundException;
import com.szampchat.server.community.exception.NotOwnerException;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.role.RoleService;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.shared.CustomPrincipalProvider;
import com.szampchat.server.upload.FilePath;
import com.szampchat.server.upload.FileStorageService;
import com.szampchat.server.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        //Collect all channels of community
        Mono<List<Channel>> channelFlux = channelService.findChannelsForCommunity(communityId).collectList();
        //Collect members with corresponding roles
        Mono<List<CommunityMemberRolesDTO>> memberFlux = communityMemberService.getCommunityMembersWithRoles(communityId).collectList();
        //Collect roles
        Mono<List<Role>> roleFlux = roleService.findRolesForCommunity(communityId).collectList();

        return findById(communityId)
            .flatMap(community -> Mono.zip(channelFlux, memberFlux, roleFlux)
                    .map(data -> new FullCommunityInfoDTO(community, data.getT1(), data.getT2(), data.getT3()))
            );
    }

    //remove?
    public Flux<Community> getAllCommunities(){
        return communityRepository.findAll();
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
                    .build();

        return communityRepository.save(community)
            .switchIfEmpty(Mono.error(new CommunityNotFoundException()))
            .flatMap(savedCommunity -> {
                Long communityId = savedCommunity.getId();

                // After creating community its owner also need to be added as its member
                return userService.findUser(ownerId)
                    .flatMap(savedUser ->
                        communityMemberService.create(communityId, savedUser.getId())
                            .then(Mono.just(savedCommunity))
                    );
            });
        });

    }

    // TODO edit image
    public Mono<Community> editCommunity(Long id, Community community){
        return communityRepository.findById(id)
            .flatMap(existingCommunity -> {
                existingCommunity = community;
               return communityRepository.save(existingCommunity);
            });
    }

    // TODO delete image from storage
    public Mono<Void> delete(Long id){
        return communityRepository.deleteById(id)
                .doOnSuccess(_ -> log.info("Deleted community by id: {}", id));
    }
}
