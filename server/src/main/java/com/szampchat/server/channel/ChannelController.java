package com.szampchat.server.channel;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.channel.dto.ChannelCreateDTO;
import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.community.service.CommunityMemberService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class ChannelController {
    private final ChannelService channelService;
    private final ModelMapper modelMapper;

    @GetMapping("/communities/{communityId}/channels")
    @PreAuthorize("@communityMemberService.isMember(#communityId, #currentUser.userId)")
    public Flux<ChannelDTO> getChannelsForCommunity(@PathVariable Long communityId, CurrentUser currentUser) {
        return channelService.findChannelsForCommunity(communityId)
                .map(channel -> modelMapper.map(channel, ChannelDTO.class));
    }

    @PostMapping("/channels")
    @PreAuthorize("@communityMemberService.isMember(#channelCreateDTO.communityId, #currentUser.userId)")
    //TODO Check if user has permission to create channels in community
    public Mono<Channel> createChannel(@RequestBody ChannelCreateDTO channelCreateDTO, CurrentUser currentUser) {
        return channelService.createChannel(channelCreateDTO);
    }

    @PatchMapping("/channels/{channelId}")
    @PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    //Check if user has permission to edit this channel
    public Mono<Channel> editChannel(@PathVariable("channelId") Long channelId, Channel channel, @Param("currentUser") CurrentUser currentUser) {
        return channelService.editChannel(channelId, channel);
    }

    @DeleteMapping("/channels/{channelId}")
    @PreAuthorize("@channelService.isParticipant(#channelId, #currentUser.userId)")
    //Check if user has permission to delete this channel
    public Mono<Void> deleteChannel(@PathVariable Long channelId, CurrentUser currentUser) {
        return channelService.deleteChannel(channelId);
    }
}
