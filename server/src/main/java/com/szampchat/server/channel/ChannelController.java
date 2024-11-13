package com.szampchat.server.channel;

import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.dto.request.ChannelCreateRequest;
import com.szampchat.server.channel.dto.request.ChannelEditRequest;
import com.szampchat.server.auth.annotation.HasPermission;
import com.szampchat.server.auth.annotation.ResourceId;
import com.szampchat.server.permission.data.PermissionScope;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.shared.docs.OperationDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.szampchat.server.shared.docs.DocsProperties.*;
import static com.szampchat.server.shared.docs.DocsProperties.RESPONSE_401;

@Tag(name = "Channel")
@SecurityRequirement(name = "OAuthSecurity")

@AllArgsConstructor
@RestController
public class ChannelController {
    private final ChannelService channelService;

    @ApiResponse(responseCode = "201")
    @OperationDocs({RESPONSE_419, REQUIRES_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Create channel")

    @HasPermission(scope = PermissionScope.COMMUNITY, value = PermissionFlag.CHANNEL_CREATE)
    @PreAuthorize("@auth.canAccess(#communityId, 'COMMUNITY')")
    @PostMapping("/communities/{communityId}/channels")
    public Mono<ChannelDTO> createChannel(@RequestBody ChannelCreateRequest channelCreateRequest,
                                       @ResourceId @PathVariable Long communityId) {
        return channelService.createChannel(channelCreateRequest, communityId);
    }


    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Edit channel")

    @HasPermission(scope = PermissionScope.CHANNEL, value = PermissionFlag.CHANNEL_MODIFY)
    @PreAuthorize("@auth.canAccess(#channelId, 'CHANNEL')")
    @PutMapping("/channels/{channelId}")
    public Mono<ChannelDTO> editChannel(@PathVariable Long channelId,
                                        @ResourceId @Valid @RequestBody ChannelEditRequest channel) {
        return channelService.editChannel(channelId, channel);
    }


    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Delete channel")

    @HasPermission(scope = PermissionScope.CHANNEL, value = PermissionFlag.CHANNEL_MODIFY)
    @PreAuthorize("@auth.canAccess(#channelId, 'CHANNEL')")
    @DeleteMapping("/channels/{channelId}")
    public Mono<Void> deleteChannel(@PathVariable Long channelId) {
        return channelService.deleteChannel(channelId);
    }
}
