package com.szampchat.server.community.service;

import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.dto.InvitationResponseDTO;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.entity.Invitation;
import com.szampchat.server.community.exception.InvalidInvitationException;
import com.szampchat.server.community.repository.InvitationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class InvitationService {
    private InvitationRepository invitationRepository;
    private CommunityService communityService;
    private CommunityMemberService communityMemberService;


    // Link is create without domain name
    // I'm not sure if it should be added here or at the frontend
    // TODO UUID instead of
    public Mono<InvitationResponseDTO> createInvitation(Long id, Integer days){
        Invitation invitation = Invitation
                .builder()
                .communityId(id)
                .expiredAt(LocalDateTime.now().plusDays(days))
                .build();

        return invitationRepository.save(invitation)
                .flatMap(inv -> Mono.just(new InvitationResponseDTO(inv.toLink())));
    }

    public Mono<CommunityDTO> getInvitationInfo(Long communityId, Long invitationId){
        return invitationRepository.isValid(invitationId, communityId)
            .flatMap(isValid -> {
                if(!isValid){
                    return Mono.error(new InvalidInvitationException(invitationId));
                } else {
                    return communityService.getById(communityId);
                }
            });
    }

    public Mono<CommunityMember> addMemberToCommunity(Long communityId, Long invitationId, Long userId){
        // check if invitation is valid
        // for now link won't be deleted from db after accepting invitation
        //TODO add one time invitation?
        //TODO check if already exists here
        return invitationRepository.isValid(invitationId, communityId)
                .flatMap(isValid -> {
                    if(!isValid) {
                        return Mono.error(new InvalidInvitationException(invitationId));
                    }

                    // add invited user as member of community
                    return communityMemberService.create(communityId, userId);
                });
    }

    // Single invitation can be used to invite multiple users, and it's only limited by it expired date
    // Most efficient way to delete outdated invitations which cannot be used anymore is to set up cron task
    // which will check for outdated invitations once every day
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteOutdatedInvitations(){
        invitationRepository.deleteAllByExpiredAt().doOnSuccess(rows -> log.info("Deleted: " + rows + "rows"));//TODO not subscribed to published / won't work
    }

}
