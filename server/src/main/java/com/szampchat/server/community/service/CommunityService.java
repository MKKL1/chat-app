package com.szampchat.server.community.service;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.community.dto.CommunityCreateDTO;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.entity.Invitation;
import com.szampchat.server.community.exception.CommunityNotFoundException;
import com.szampchat.server.community.exception.InvalidInvitationException;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.community.repository.InvitationRepository;
import com.szampchat.server.user.UserService;
import com.szampchat.server.user.entity.User;
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

    public Mono<Community> findById(Long id) {
        return communityRepository.findById(id)
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()));
    }

    public Mono<Boolean> isOwner(Long communityId, Long userId) {
        return communityRepository.isOwnerOfCommunity(communityId, userId);
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
    public Mono<String> createInvitation(Long id, Integer days){
        Invitation invitation = Invitation
                .builder()
                .communityId(id)
                .expiredAt(LocalDateTime.now().plusDays(days))
                .build();

        return invitationRepository.save(invitation)
                .flatMap(inv -> Mono.just(inv.toLink()));
    }

    public Mono<CommunityMember> addMemberToCommunity(Long communityId, Long invitationId, Long userId){
        // check if invitation is valid
        return invitationRepository.isValid(invitationId, communityId).flatMap(isValid -> {
            if(!isValid) {
                return Mono.error(new InvalidInvitationException());
            }

            // add invited user as member of community
            return communityMemberService.create(communityId, userId);
        });
    }

    // Maybe also add owner as member of community?
    // I don't know if I am using Mono in right way
    // TODO store image url
    public Mono<CommunityDTO> save(Community community, CurrentUser user){
        community.setOwner_id(user.getUserId());

        Mono<Community> communityMono = communityRepository.save(community)
                .switchIfEmpty(Mono.error(new Exception()));
        Mono<User> userMono = userService.findUser(user.getUserId())
                .switchIfEmpty(Mono.error(new Exception()));

        return communityMono.flatMap(c -> userMono.map(u -> new CommunityDTO(c, u)));
    }
}
