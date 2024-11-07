package com.szampchat.server.channel;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.dto.request.ChannelCreateRequest;
import com.szampchat.server.channel.dto.request.ChannelEditRequest;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.shared.docs.OperationDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.validation.annotation.Validated;
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

    //TODO change path of this endpoint? /communities/{}/channels
    @PostMapping("/channels/{communityId}")
    public Mono<ChannelDTO> createChannel(@RequestBody ChannelCreateRequest channelCreateRequest,
                                       @PathVariable Long communityId) {
        return channelService.createChannel(channelCreateRequest, communityId);
    }


    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Edit channel")

    @PutMapping("/channels/{channelId}")
    //@PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    //Check if user has permission to edit this channel
    public Mono<ChannelDTO> editChannel(@PathVariable Long channelId,
                                        @Valid @RequestBody ChannelEditRequest channel) {
        return channelService.editChannel(channelId, channel);
    }


    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Delete channel")

    @DeleteMapping("/channels/{channelId}")
    public Mono<Void> deleteChannel(@PathVariable Long channelId) {
        return channelService.deleteChannel(channelId);
    }
}
