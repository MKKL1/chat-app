package com.szampchat.server.channel;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.channel.repository.ChannelRepository;
import com.szampchat.server.community.service.CommunityMemberService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("channelService")
@AllArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final CommunityMemberService communityMemberService;

    //TODO cache it (this method will be called on most api operations)
    public Mono<Boolean> isParticipant(Long channelId, Long userId) {
        return getChannel(channelId)
                .flatMap(channel -> communityMemberService.isMember(channel.getCommunity(), userId));
    }

    public Mono<Channel> getChannel(Long channelId) {
        return channelRepository.findById(channelId);
    }

    public Flux<Channel> findChannelsForCommunity(Long communityId) {
        return channelRepository.findChannelsByCommunity(communityId);
    }
}
