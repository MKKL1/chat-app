package com.szampchat.server.channel;

import com.szampchat.server.channel.entity.Channel;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@RestController
public class ChannelController {
    private final ChannelService channelService;

    @GetMapping("/communities/{communityId}/channels")
    public Flux<Channel> getChannelsForCommunity(@PathVariable Long communityId) {
        return channelService.findChannelsForCommunity(communityId);
    }
}
