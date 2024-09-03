package com.szampchat.server.community;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.community.dto.CommunityCreateDTO;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.dto.CommunityEditDTO;
import com.szampchat.server.community.dto.CommunityMemberDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
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

    // Maybe instead of dozens of small request it will be better to make
    // huge request which will get all data about community?
    @GetMapping("/{communityId}/members")
    @PreAuthorize("@communityMemberService.isMember(#communityId, #currentUser.userId)")
    public Flux<CommunityMemberDTO> getCommunityMembers(@PathVariable Long communityId, CurrentUser currentUser) {
        return communityMemberService.getCommunityMembers(communityId);
    }

    // only for testing
//    @GetMapping()
//    public Flux<Community> getAllCommunities(){
//        return communityService.getAllCommunities();
//    }

    @GetMapping()
    public Flux<Community> getUserCommunities(CurrentUser user){
        return communityService.getUserCommunities(user.getUserId());
    }

    @GetMapping("/owned")
    public Flux<Community> getOwnedCommunities(CurrentUser user){
        return communityService.getOwnedCommunities(user.getUserId());
    }

    // this endpoint will create link to community which then can be shared with other users to join your community
    @PostMapping("/{communityId}/invite")
    @PreAuthorize("@communityService.isOwner(#communityId, #currentUser.userId)")
    public Mono<String> inviteToCommunity(@PathVariable Long communityId, @RequestBody @NotNull @Min(1) Integer days, CurrentUser currentUser){
        return communityService.createInvitation(communityId, days);
    }

    // What if user is already in community?
    //TODO
    //For when user accepts invite link
    //First idea: User is shown a form when they click invite link, then when button is pressed, request is sent to this endpoint
    //            To make sure that user used invite link to join this community, invite's id has to be provided with this request
    //            When invite link expires, the provided id will no longed be valid -> 403 / 404
    //Maybe invite id should be something less repeatable then snowflake, maybe use uuid
    // Don't use @RequestParam in @PostMapping
    @PostMapping("/{communityId}/join")
    public Mono<CommunityMember> joinCommunity(@PathVariable Long communityId, @RequestBody Long invitationId, CurrentUser currentUser) {
        return communityService.addMemberToCommunity(communityId, invitationId, currentUser.getUserId());
    }

    //Everyone can create community, no authorization, or at least limit one user to having 10 communities TODO?
    @PostMapping()
    public Mono<CommunityDTO> createCommunity(@RequestBody Community community, CurrentUser user) {
        return communityService.save(community, user);
    }

    @PatchMapping("/{communityId}")
    @PreAuthorize("@communityService.isOwner(#communityId, #currentUser.userId)")
    public Mono<CommunityDTO> editCommunity(@PathVariable Long communityId, @RequestBody CommunityEditDTO communityEditDTO, CurrentUser currentUser) {
        return Mono.empty();
    }

    @DeleteMapping("/{communityId}")
    @PreAuthorize("@communityService.isOwner(#communityId, #currentUser.userId)")
    public Mono<Void> deleteCommunity(@PathVariable Long communityId, CurrentUser currentUser) {
        return Mono.empty();
    }
}
