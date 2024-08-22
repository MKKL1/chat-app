package com.szampchat.server.channel;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.channel.repository.ChannelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;

    public Flux<Channel> findChannelsForCommunity(Long communityId) {
        return channelRepository.findChannelsByCommunity(communityId);
    }
}
