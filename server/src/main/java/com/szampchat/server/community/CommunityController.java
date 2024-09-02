package com.szampchat.server.community;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.community.dto.CommunityCreateDTO;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.dto.CommunityEditDTO;
import com.szampchat.server.community.dto.CommunityMemberDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/communities")
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

    @GetMapping("/{communityId}/members")
    @PreAuthorize("@communityMemberService.isMember(#communityId, #currentUser.userId)")
    public Flux<CommunityMemberDTO> getCommunityMembers(@PathVariable Long communityId, CurrentUser currentUser) {
        return communityMemberService.getCommunityMembers(communityId);
    }

    // only for testing
    @GetMapping()
    public Flux<Community> getAllCommunities(){
        return communityService.getAllCommunities();
    }

    // TODO implement and use instead of getAllCommunities
//    @GetMapping()
//    public Flux<Community> getUserCommunities(CurrentUser user){
//        return Flux.empty();
//    }

    // Implement A$AP
    //TODO
    //For when user accepts invite link
    //First idea: User is shown a form when they click invite link, then when button is pressed, request is sent to this endpoint
    //            To make sure that user used invite link to join this community, invite's id has to be provided with this request
    //            When invite link expires, the provided id will no longed be valid -> 403 / 404
    //Maybe invite id should be something less repeatable then snowflake, maybe use uuid
    @PostMapping("/{communityId}/join")
    public Mono<Void> joinCommunity(@PathVariable Long communityId, @RequestParam Long inviteId, CurrentUser currentUser) {
        return Mono.empty();
    }

    //Everyone can create community, no authorization, or at least limit one user to having 10 communities TODO?
    @PostMapping()
    public Mono<Community> createCommunity(@RequestBody Community community, CurrentUser user) {
        return communityService.save(community, user);
    }

    @PatchMapping("/{communityId}")
    public Mono<CommunityDTO> editCommunity(@PathVariable Long communityId, @RequestBody CommunityEditDTO communityEditDTO) {
        return Mono.empty();
    }

    @DeleteMapping("/{communityId}")
    public Mono<Void> deleteCommunity(@PathVariable Long communityId) {
        return Mono.empty();
    }
}
