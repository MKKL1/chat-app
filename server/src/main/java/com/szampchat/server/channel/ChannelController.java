package com.szampchat.server.channel;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.channel.dto.ChannelCreateDTO;
import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.shared.docs.OperationDocs;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.szampchat.server.shared.docs.DocsProperties.*;
import static com.szampchat.server.shared.docs.DocsProperties.RESPONSE_401;

@Tag(name = "Channel")
@SecurityRequirement(name = "OAuthSecurity")

@AllArgsConstructor
@RestController
public class ChannelController {
    private final ChannelService channelService;


    //This endpoint is redundant as this data is sent with communities/:communityId/info
//    @ApiResponse(responseCode = "200")
//    @OperationDocs({RESPONSE_419, REQUIRES_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
//    @Operation(summary = "Get community channels")
//
//    @GetMapping("/communities/{communityId}/channels")
//    @PreAuthorize("@communityMemberService.isMember(#communityId, #currentUser.userId)")
//    public Flux<ChannelDTO> getChannelsForCommunity(@PathVariable Long communityId, CurrentUser currentUser) {
//        return channelService.findChannelsForCommunity(communityId)
//                .map(channel -> modelMapper.map(channel, ChannelDTO.class));
//    }

    //TODO broken
    //TODO Check if user has permission to create channels in community

    @ApiResponse(responseCode = "201")
    @OperationDocs({RESPONSE_419, REQUIRES_MEMBER_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Create channel")

    @PostMapping("/channels/{communityId}")
    //@PreAuthorize("@communityMemberService.isMember(#channelCreateDTO.communityId, #currentUser.userId)") //TODO why is this commented?
    public Mono<Channel> createChannel(@RequestBody ChannelCreateDTO channelCreateDTO) {
        return channelService.createChannel(channelCreateDTO);
    }


    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Edit channel")

    @PutMapping("/channels/{channelId}")
    //@PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    //Check if user has permission to edit this channel
    public Mono<Channel> editChannel(@PathVariable("channelId") Long channelId, @RequestBody Channel channel, @Param("currentUser") CurrentUser currentUser) {
        return channelService.editChannel(channelId, channel);
    }


    @ApiResponse(responseCode = "204")
    @OperationDocs({RESPONSE_419, REQUIRES_PARTICIPANT_PERMISSION, DOCUMENT_PATH_VARIABLES, RESPONSE_401})
    @Operation(summary = "Delete channel")

    @DeleteMapping("/channels/{channelId}")
//    @PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    //Check if user has permission to delete this channel
    public Mono<Void> deleteChannel(@PathVariable Long channelId, CurrentUser currentUser) {
        return channelService.deleteChannel(channelId);
    }
}
