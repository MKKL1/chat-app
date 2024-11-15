package com.szampchat.server.community;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.auth.annotation.HasPermission;
import com.szampchat.server.auth.annotation.ResourceId;
import com.szampchat.server.community.dto.*;
import com.szampchat.server.community.dto.request.CommunityCreateRequest;
import com.szampchat.server.community.dto.request.CommunityEditRequest;
import com.szampchat.server.community.dto.request.JoinRequest;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.community.service.InvitationService;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.permission.data.PermissionScope;
import com.szampchat.server.shared.docs.OperationDocs;
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
    private final InvitationService invitationService;


    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get community")

    @PreAuthorize("@auth.canAccess(#communityId, 'COMMUNITY')")
    @GetMapping("/{communityId}")
    public Mono<CommunityDTO> getCommunity(@PathVariable Long communityId) {
        return communityService.getById(communityId);
    }

    @ApiResponse(responseCode = "200")
    @OperationDocs({RESPONSE_419, REQUIRES_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Get full community",
            description = """
                    Retrieve all required information about community.
                    Ideally it should be used once when opening community
                    and then updated using websocket events""")

    @PreAuthorize("@auth.canAccess(#communityId, 'COMMUNITY')")
    @GetMapping("/{communityId}/info")
//    @PreAuthorize("@communityMemberService.isMember(#communityId)")
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
    public Flux<CommunityDTO> getUserCommunities(CurrentUser user){
        return communityService.getUserCommunities(user.getUserId());
    }


    @ApiResponse(responseCode = "201")
    @OperationDocs({RESPONSE_419, RESPONSE_401, REQUIRES_OWNER_PERMISSION, DOCUMENT_PATH_VARIABLES})
    @Operation(summary = "Create invite link",
            description = """
                    Creates temporary or permanent(?) invitation link to community.
                    This invitation can then be shared with any user, who can then join given community""")

    @HasPermission(scope = PermissionScope.COMMUNITY, value = PermissionFlag.INVITE_CREATE)
    @PreAuthorize("@auth.canAccess(#communityId, 'COMMUNITY')")
    @PostMapping("/{communityId}/invite")
    public Mono<InvitationResponseDTO> inviteToCommunity(@ResourceId @PathVariable Long communityId){
        return invitationService.createInvitation(communityId, 5);
    }

    @ApiResponse(responseCode = "201")
    @OperationDocs({RESPONSE_419, RESPONSE_401, REQUIRES_NOT_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES})
    @Operation(summary = "Join community",
            description = """
                    Uses invitation link (it's id) to join community as current user.
                    This action result in expiration of invitation. (?)""")

    //TODO not sure what should be response
    @PostMapping("/{communityId}/join")
    public Mono<CommunityMember> joinCommunity(@PathVariable Long communityId, @RequestBody JoinRequest joinRequest, CurrentUser currentUser) {
        return invitationService.addMemberToCommunity(communityId, joinRequest.invitationId(), currentUser.getUserId());
    }

    @ApiResponse(responseCode = "201")
    @OperationDocs({RESPONSE_419, RESPONSE_401})
    @Operation(summary = "Create community",
            description = """
                    Creates new community with current user as a owner.
                    New members can be invited using (link to other endpoints)""")

    //Everyone can create community, no authorization, or at least limit one user to having 10 communities TODO?
    @PostMapping()
    public Mono<Community> createCommunity(
            @RequestPart("community") CommunityCreateRequest community,
            @RequestPart(value = "file", required = false) FilePart file,
            CurrentUser user) {
        return communityService.save(community, file, user.getUserId());
    }

    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, RESPONSE_401, REQUIRES_OWNER_PERMISSION, DOCUMENT_PATH_VARIABLES})
    @Operation(summary = "Edit community",
            description = """
                    Edits community""")

    @HasPermission(scope = PermissionScope.COMMUNITY, value = PermissionFlag.ADMINISTRATOR)
    @PreAuthorize("@auth.canAccess(#communityId, 'COMMUNITY')")
    @PatchMapping("/{communityId}")
    public Mono<CommunityDTO> editCommunity(
            @ResourceId @PathVariable Long communityId,
            @RequestPart("community") CommunityEditRequest request,
            @RequestPart("file") FilePart file) {
        return communityService.editCommunity(communityId, request, file);
    }

    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, RESPONSE_401, REQUIRES_OWNER_PERMISSION, DOCUMENT_PATH_VARIABLES})
    @Operation(summary = "Delete community",
            description = """
                    Removes community""")

    //TODO isOwner method here
    @DeleteMapping("/{communityId}")
    public Mono<Void> deleteCommunity(@PathVariable Long communityId) {
        return communityService.delete(communityId);
    }
}
