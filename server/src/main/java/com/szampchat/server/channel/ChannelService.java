package com.szampchat.server.channel;

import com.szampchat.server.channel.dto.ChannelCreateDTO;
import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.channel.repository.ChannelRepository;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.snowflake.Snowflake;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("channelService")
@AllArgsConstructor
public class ChannelService {
    private final ChannelRepository channelRepository;
    private final CommunityMemberService communityMemberService;
    private final Snowflake snowflake;

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

    public Mono<Channel> createChannel(ChannelCreateDTO channel){
        return channelRepository.save(
                Channel.builder()
                        .id(snowflake.nextId())
                        .name(channel.getName())
                        .community(channel.getCommunityId())
                        .type(channel.getType().getValue())
                        .build()
        );
    }

    public Mono<Channel> editChannel(Long id, Channel channel){
        return channelRepository.findById(id)
            .flatMap(existingChannel -> {
               existingChannel = channel;
               return channelRepository.save(existingChannel);
            });
    }

    // issue with constraints
    public Mono<Void> deleteChannel(Long id){
        return channelRepository.deleteById(id);
    }

}
