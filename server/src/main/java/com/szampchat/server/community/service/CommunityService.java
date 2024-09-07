package com.szampchat.server.community.service;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.community.dto.CommunityCreateDTO;
import com.szampchat.server.community.dto.InvitationResponseDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.entity.Invitation;
import com.szampchat.server.community.exception.CommunityNotFoundException;
import com.szampchat.server.community.exception.InvalidInvitationException;
import com.szampchat.server.community.exception.NotOwnerException;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.community.repository.InvitationRepository;
import com.szampchat.server.snowflake.Snowflake;
import com.szampchat.server.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final InvitationRepository invitationRepository;
    private final UserService userService;
    private final CommunityMemberService communityMemberService;
    private final Snowflake snowflake;

    public Mono<Community> findById(Long id) {
        return communityRepository.findById(id)
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()));
    }

    public Mono<Boolean> isOwner(Long communityId, Long userId) {
        return communityRepository.isOwnerOfCommunity(communityId, userId).flatMap(
            owner -> owner ? Mono.just(true) : Mono.error(new NotOwnerException())
        );
    }

    public Flux<Community> getAllCommunities(){
        return communityRepository.findAll();
    }

    public Flux<Community> getUserCommunities(Long id){
        return communityRepository.userCommunities(id);
    }

    public Flux<Community> getOwnedCommunities(Long id){
        return communityRepository.ownedCommunities(id);
    }

    // Link is create without domain name
    // I'm not sure if it should be added here or at the frontend
    public Mono<InvitationResponseDTO> createInvitation(Long id, Integer days){
        Invitation invitation = Invitation
                .builder()
                .communityId(id)
                .expiredAt(LocalDateTime.now().plusDays(days))
                .build();

        return invitationRepository.save(invitation)
                .flatMap(inv -> Mono.just(new InvitationResponseDTO(inv.toLink())));
    }


    // TODO handle errors
    public Mono<CommunityMember> addMemberToCommunity(Long communityId, Long invitationId, Long userId){
        // check if invitation is valid
        // for now link won't be deleted from db after accepting invitation
        return invitationRepository.isValid(invitationId, communityId).flatMap(isValid -> {
            if(!isValid) {
                return Mono.error(new InvalidInvitationException());
            }

            // add invited user as member of community
            return communityMemberService.create(communityId, userId);
        });
    }

    // TODO store image url
    public Mono<Community> save(CommunityCreateDTO communityDTO, CurrentUser user) {
        Community community = Community.builder()
                .name(communityDTO.name())
                .ownerId(user.getUserId())
                .build();

        return communityRepository.save(community)
            .switchIfEmpty(Mono.error(new Exception()))
            .flatMap(savedCommunity -> {
                Long communityId = savedCommunity.getId();

                // After creating community its owner also need to be added as its member
                return userService.findUser(user.getUserId())
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

    // won't work because of constraints
    // cannot set cascade deleting because r2dbc
    public Mono<Void> delete(Long id){
        return communityRepository.deleteById(id).doOnSuccess(_ -> System.out.println("Deleted community"));
    }
}
