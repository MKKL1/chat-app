package com.szampchat.server.community;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.community.dto.*;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.community.service.InvitationService;
import com.szampchat.server.shared.docs.OperationDocs;
import com.szampchat.server.upload.FilePath;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.szampchat.server.shared.docs.DocsProperties.*;


@Tag(name = "Community")
@SecurityRequirement(name = "OAuthSecurity")

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/communities")
@Validated //?
public class CommunityController {
    private final CommunityService communityService;
    private final CommunityMemberService communityMemberService;
    private final InvitationService invitationService;


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get community")

    @GetMapping("/{communityId}")
    @PreAuthorize("@communityMemberService.isMember(#communityId)")
    public Mono<CommunityDTO> getCommunity(@PathVariable Long communityId) {
        return communityService.findById(communityId);
    }

    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get full community",
            description = """
                    Retrieve all required information about community.
                    Ideally it should be used once when opening community
                    and then updated using websocket events""")

    @GetMapping("/{communityId}/info")
    @PreAuthorize("@communityMemberService.isMember(#communityId)")
    public Mono<FullCommunityInfoDTO> getFullCommunityInfo(@PathVariable Long communityId){
        return communityService.getFullCommunityInfo(communityId);
    }

    //TODO DTO
    //TODO can be protected by oauth scope
    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, RESPONSE_401})
    @Operation(summary = "Get user's communities",
            description = "Retrieves communities of logged in user")

    @GetMapping()
    public Flux<Community> getUserCommunities(CurrentUser user){
        return communityService.getUserCommunities(user.getUserId());
    }


    @ApiResponse(responseCode = "201")
    @OperationDocs({RESPONSE_419, RESPONSE_401, REQUIRES_OWNER_PERMISSION, DOCUMENT_PATH_VARIABLES})
    @Operation(summary = "Create invite link",
            description = """
                    Creates temporary or permanent(?) invitation link to community.
                    This invitation can then be shared with any user, who can then join given community""")

    @PostMapping("/{communityId}/invite")
    @PreAuthorize("@communityService.isOwner(#communityId)")
    public Mono<InvitationResponseDTO> inviteToCommunity(@PathVariable Long communityId){
        return invitationService.createInvitation(communityId, 5);
    }

    @ApiResponse(responseCode = "201")
    @OperationDocs({RESPONSE_419, RESPONSE_401, REQUIRES_NOT_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES})
    @Operation(summary = "Join community",
            description = """
                    Uses invitation link (it's id) to join community as current user.
                    This action result in expiration of invitation. (?)""")

    @PostMapping("/{communityId}/join")
    @PreAuthorize("@communityMemberService.isNotMember(#communityId)")
    public Mono<CommunityMember> joinCommunity(@PathVariable Long communityId, @RequestBody JoinRequestDTO joinRequestDTO, CurrentUser currentUser) {
        return invitationService.addMemberToCommunity(communityId, joinRequestDTO.invitationId(), currentUser.getUserId());
    }

    @ApiResponse(responseCode = "201")
    @OperationDocs({RESPONSE_419, RESPONSE_401})
    @Operation(summary = "Create community",
            description = """
                    Creates new community with current user as a owner.
                    New members can be invited using (link to other endpoints)""")

    //Everyone can create community, no authorization, or at least limit one user to having 10 communities TODO?
    //add in form data:
    //Key: community, Value: { "name": "My community" }
    @PostMapping()
    public Mono<Community> createCommunity(
            @RequestPart("community") CommunityCreateDTO community,
            @RequestPart(value = "file", required = false) FilePart file,
            CurrentUser user) {
        return communityService.save(community, file, user.getUserId());
    }

    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, RESPONSE_401, REQUIRES_OWNER_PERMISSION, DOCUMENT_PATH_VARIABLES})
    @Operation(summary = "Edit community",
            description = """
                    Edits community""")
    //Use community dto?
    @PatchMapping("/{communityId}")
    @PreAuthorize("@communityService.isOwner(#communityId)")
    public Mono<Community> editCommunity(@PathVariable Long communityId, @RequestBody Community community) {
        return communityService.editCommunity(communityId, community);
    }

    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, RESPONSE_401, REQUIRES_OWNER_PERMISSION, DOCUMENT_PATH_VARIABLES})
    @Operation(summary = "Delete community",
            description = """
                    Removes community""")

    @DeleteMapping("/{communityId}")
    @PreAuthorize("@communityService.isOwner(#communityId)")
    public Mono<Void> deleteCommunity(@PathVariable Long communityId) {
        return communityService.delete(communityId);
    }
}
