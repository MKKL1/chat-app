package com.szampchat.server.channel;

import com.szampchat.server.channel.dto.ChannelCreateDTO;
import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.entity.Channel;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
public class ChannelController {
    private final ChannelService channelService;
    private final ModelMapper modelMapper;

    @GetMapping("/communities/{communityId}/channels")
    public Flux<ChannelDTO> getChannelsForCommunity(@PathVariable Long communityId) {
        return channelService.findChannelsForCommunity(communityId)
                .map(channel -> modelMapper.map(channel, ChannelDTO.class));
    }

    @PostMapping("/channels")
    public Mono<ChannelDTO> createChannel(@RequestBody ChannelCreateDTO channelCreateDTO) {
        return Mono.empty();
    }

    @PatchMapping("/channels/{channelId}")
    public Mono<Channel> editChannel(@PathVariable Long channelId) {
        return Mono.empty();
    }

    @DeleteMapping("/channels/{channelId}")
    public Mono<Void> deleteChannel(@PathVariable Long channelId) {
        return Mono.empty();
    }
}
