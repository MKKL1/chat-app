package com.szampchat.server.community.service;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.community.dto.CommunityCreateDTO;
import com.szampchat.server.community.dto.CommunityMemberDTO;
import com.szampchat.server.community.dto.FullCommunityInfoDTO;
import com.szampchat.server.community.dto.InvitationResponseDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.entity.Invitation;
import com.szampchat.server.community.exception.CommunityNotFoundException;
import com.szampchat.server.community.exception.InvalidInvitationException;
import com.szampchat.server.community.exception.NotOwnerException;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.community.repository.InvitationRepository;
import com.szampchat.server.role.RoleService;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.snowflake.Snowflake;
import com.szampchat.server.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Member;
import java.time.LocalDateTime;
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

    public Mono<Community> findById(Long id) {
        return communityRepository.findById(id)
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()));
    }

    public Mono<Boolean> isOwner(Long communityId, Long userId) {
        return communityRepository.isOwnerOfCommunity(communityId, userId).flatMap(
            owner -> owner ? Mono.just(true) : Mono.error(new NotOwnerException())
        );
    }

    // for now, I just join together few entities
    // TODO optimize this
    // channels returned properly
    // members not
    // I don't checked roles
    public Mono<FullCommunityInfoDTO> getFullCommunityInfo(Long id){
        //TODO why not just use findById from CommunityService
        return communityRepository.findById(id)
            .switchIfEmpty(Mono.error(new CommunityNotFoundException()))
            .flatMap(community -> {
                Mono<List<Channel>> channelFlux = channelService.findChannelsForCommunity(community.getId()).collectList();
                Mono<List<CommunityMemberDTO>> memberFlux = communityMemberService.getCommunityMembers(community.getId()).collectList();
                Mono<List<Role>> roleFlux = roleService.findRolesForCommunity(community.getId()).collectList();

                return Mono.zip(channelFlux, memberFlux, roleFlux).flatMap(data ->
                    Mono.just(new FullCommunityInfoDTO(community, data.getT1(), data.getT2(), data.getT3()))
                );
            });
    }

    public Flux<Community> getAllCommunities(){
        return communityRepository.findAll();
    }

    public Flux<Community> getUserCommunities(Long id){
        return communityRepository.userCommunities(id);
    }


    // TODO store image url
    public Mono<Community> save(CommunityCreateDTO communityDTO, Long ownerId) {
        Community community = Community.builder()
                .name(communityDTO.name())
                .ownerId(ownerId)
                .build();

        return communityRepository.save(community)
            .switchIfEmpty(Mono.error(new Exception()))
            .flatMap(savedCommunity -> {
                Long communityId = savedCommunity.getId();

                // After creating community its owner also need to be added as its member
                return userService.findUser(ownerId)
                    .switchIfEmpty(Mono.error(new Exception("User not found")))
                    .flatMap(savedUser -> communityMemberService.create(communityId, savedUser.getId())
                        .then(Mono.just(community)));
            });
    }

    public Mono<Community> editCommunity(Long id, Community community){
        return communityRepository.findById(id)
            .flatMap(existingCommunity -> {
                existingCommunity = community;
               return communityRepository.save(existingCommunity);
            });
    }

    public Mono<Void> delete(Long id){
        return communityRepository.deleteById(id)
                .doOnSuccess(_ -> log.info("Deleted community by id: {}", id));
    }
}
