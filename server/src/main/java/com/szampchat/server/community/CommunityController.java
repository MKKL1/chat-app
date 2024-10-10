package com.szampchat.server.community;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.community.dto.*;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.community.service.InvitationService;
import com.szampchat.server.shared.docs.ApiResponseOK;
import com.szampchat.server.shared.docs.DocsProperties;
import com.szampchat.server.shared.docs.OperationDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.szampchat.server.shared.docs.DocsProperties.*;


@Tag(name = "Community")

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/communities")
@Validated //?
public class CommunityController {
    private final CommunityService communityService;
    private final CommunityMemberService communityMemberService;
    private final InvitationService invitationService;


    @ApiResponseOK
    @OperationDocs({RESPONSE_419, REQUIRES_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES})
    @Operation(summary = "Get community")

    @GetMapping("/{communityId}")
    @PreAuthorize("@communityMemberService.isMember(#communityId)")
    public Mono<CommunityDTO> getCommunity(@PathVariable Long communityId) {
        return communityService.findById(communityId);
    }

    @ApiResponseOK
    @OperationDocs({RESPONSE_419, REQUIRES_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES})
    @Operation(summary = "Get full community")

    @GetMapping("/{communityId}/info")
    @PreAuthorize("@communityMemberService.isMember(#communityId)")
    public Mono<FullCommunityInfoDTO> getFullCommunityInfo(@PathVariable Long communityId){
        return communityService.getFullCommunityInfo(communityId);
    }

    //TODO remove?
    //Maybe instead of dozens of small request it will be better to make
    //huge request which will get all data about community?
    @GetMapping("/{communityId}/members")
    @PreAuthorize("@communityMemberService.isMember(#communityId)")
    public Flux<CommunityMemberRolesDTO> getCommunityMembers(@PathVariable Long communityId) {
        return communityMemberService.getCommunityMembersWithRoles(communityId);
    }

    //TODO remove?
    //only for testing
    @PostMapping("/{communityId}")
    public Mono<CommunityMember> addMember(@PathVariable Long communityId, CurrentUser currentUser){
        return communityMemberService.create(communityId, currentUser.getUserId());
    }

    //TODO document
    @GetMapping()
    public Flux<Community> getUserCommunities(CurrentUser user){
        return communityService.getUserCommunities(user.getUserId());
    }

    //TODO document
    //this endpoint will create link to community which then can be shared with other users to join given community
    @PostMapping("/{communityId}/invite")
    @PreAuthorize("@communityService.isOwner(#communityId)")
    public Mono<InvitationResponseDTO> inviteToCommunity(@PathVariable Long communityId){
        return invitationService.createInvitation(communityId, 5);
    }

    //TODO document
    //After getting invitation link user is redirected to page with accept button
    //which will send request to this endpoint after clicking
    //I use isNotMember to ensure that user won't be added as member of community another time
    //Don't use @RequestParam in @PostMapping
    @PostMapping("/{communityId}/join")
    @PreAuthorize("@communityMemberService.isNotMember(#communityId)")
    public Mono<CommunityMember> joinCommunity(@PathVariable Long communityId, @RequestBody JoinRequestDTO joinRequestDTO, CurrentUser currentUser) {
        return invitationService.addMemberToCommunity(communityId, joinRequestDTO.invitationId(), currentUser.getUserId());
    }

    //TODO document
    //Everyone can create community, no authorization, or at least limit one user to having 10 communities TODO?
    @PostMapping()
    public Mono<Community> createCommunity(@RequestBody CommunityCreateDTO community, CurrentUser user) {
        return communityService.save(community, user.getUserId());
    }

    //TODO document
    //Use community dto?
    @PatchMapping("/{communityId}")
    @PreAuthorize("@communityService.isOwner(#communityId)")
    public Mono<Community> editCommunity(@PathVariable Long communityId, @RequestBody Community community) {
        return communityService.editCommunity(communityId, community);
    }

    //TODO document
    @DeleteMapping("/{communityId}")
    @PreAuthorize("@communityService.isOwner(#communityId)")
    public Mono<Void> deleteCommunity(@PathVariable Long communityId) {
        return communityService.delete(communityId);
    }
}
