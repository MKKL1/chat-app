package com.szampchat.server.community;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.community.dto.*;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.entity.Invitation;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.community.service.InvitationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/communities")
@Validated
public class CommunityController {
    private final CommunityService communityService;
    private final CommunityMemberService communityMemberService;
    private final InvitationService invitationService;


    // We need another endpoint with community info for user who want to join it
    //TODO It really should be CommunityDTO
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Community by given ID was not found", content = @Content),
    })
    @Operation(summary = "Get detailed info about community")
    @GetMapping("/{communityId}")
    @PreAuthorize("@communityMemberService.isMember(#communityId, #currentUser.userId)")
    public Mono<Community> getCommunity(@PathVariable Long communityId, CurrentUser currentUser) {
        return communityService.findById(communityId);
    }

    @GetMapping("/{communityId}/info")
    @PreAuthorize("@communityMemberService.isMember(#communityId, #currentUser.userId)")
    public Mono<FullCommunityInfoDTO> getFullCommunityInfo(@PathVariable Long communityId, CurrentUser currentUser){
        return communityService.getFullCommunityInfo(communityId);
    }

    // Maybe instead of dozens of small request it will be better to make
    // huge request which will get all data about community?
    @GetMapping("/{communityId}/members")
    @PreAuthorize("@communityMemberService.isMember(#communityId, #currentUser.userId)")
    public Flux<CommunityMemberDTO> getCommunityMembers(@PathVariable Long communityId, CurrentUser currentUser) {
        return communityMemberService.getCommunityMembers(communityId);
    }

    // only for testing
    @PostMapping("/{communityId}")
    public Mono<CommunityMember> addMember(@PathVariable Long communityId, CurrentUser currentUser){
        return communityMemberService.create(communityId, currentUser.getUserId());
    }

    @GetMapping()
    public Flux<Community> getUserCommunities(CurrentUser user){
        return communityService.getUserCommunities(user.getUserId());
    }


    // this endpoint will create link to community which then can be shared with other users to join your community
    @PostMapping("/{communityId}/invite")
    @PreAuthorize("@communityService.isOwner(#communityId, #currentUser.userId)")
    public Mono<InvitationResponseDTO> inviteToCommunity(@PathVariable Long communityId, @RequestBody CreateInvitationDTO invitationDTO, CurrentUser currentUser){
        return invitationService.createInvitation(communityId, invitationDTO.getDays());
    }

    // After getting invitation link user is redirected to page with accept button
    // which will send request to this endpoint after clicking
    // I use isNotMember to ensure that user won't be added as member of community another time
    // Don't use @RequestParam in @PostMapping
    @PostMapping("/{communityId}/join")
    @PreAuthorize("@communityMemberService.isNotMember(#communityId, #currentUser.userId)")
    public Mono<CommunityMember> joinCommunity(@PathVariable Long communityId, @RequestBody JoinRequestDTO joinRequestDTO, CurrentUser currentUser) {
        return invitationService.addMemberToCommunity(communityId, joinRequestDTO.invitationId(), currentUser.getUserId());
    }

    //Everyone can create community, no authorization, or at least limit one user to having 10 communities TODO?
    @PostMapping()
    public Mono<Community> createCommunity(@RequestBody CommunityCreateDTO community, CurrentUser user) {
        return communityService.save(community, user.getUserId());
    }

    @PatchMapping("/{communityId}")
    @PreAuthorize("@communityService.isOwner(#communityId, #currentUser.userId)")
    public Mono<Community> editCommunity(@PathVariable Long communityId, @RequestBody Community community, CurrentUser currentUser) {
        return communityService.editCommunity(communityId, community);
    }

    @DeleteMapping("/{communityId}")
    @PreAuthorize("@communityService.isOwner(#communityId, #currentUser.userId)")
    public Mono<Void> deleteCommunity(@PathVariable Long communityId, CurrentUser currentUser) {
        return Mono.just(communityId).doFirst(() -> System.out.println("Siema")).flatMap(communityService::delete);
    }
}
