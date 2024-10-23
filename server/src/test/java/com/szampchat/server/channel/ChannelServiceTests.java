package com.szampchat.server.channel;

import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.channel.exception.ChannelNotFoundException;
import com.szampchat.server.channel.repository.ChannelRepository;
import com.szampchat.server.community.service.CommunityMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTests {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private CommunityMemberService communityMemberService;

    @InjectMocks
    private ChannelService channelService;

    @Test
    void isParticipant_Participant() {
        Long channelId = 1L;
        Long userId = 2L;
        Long communityId = 3L;

        Channel channel = new Channel();
        channel.setCommunityId(communityId);

        when(channelRepository.findById(eq(channelId)))
                .thenReturn(Mono.just(channel));

        when(communityMemberService.isMember(eq(communityId), eq(userId)))
                .thenReturn(Mono.just(true));

        Mono<Boolean> result = channelService.isParticipant(channelId, userId);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isParticipant_NotParticipant() {
        Long channelId = 1L;
        Long userId = 2L;
        Long communityId = 3L;

        Channel channel = new Channel();
        channel.setCommunityId(communityId);

        // Mocking the channel repository to return a channel
        when(channelRepository.findById(eq(channelId)))
                .thenReturn(Mono.just(channel));

        when(communityMemberService.isMember(eq(communityId), eq(userId)))
                .thenReturn(Mono.just(false));

        Mono<Boolean> result = channelService.isParticipant(channelId, userId);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void getChannel_ChannelFound() {
        Long channelId = 1L;

        Channel channel = new Channel();

        when(channelRepository.findById(eq(channelId)))
                .thenReturn(Mono.just(channel));

        Mono<Channel> result = channelService.getChannel(channelId);

        StepVerifier.create(result)
                .expectNext(channel)
                .verifyComplete();
    }

    @Test
    void getChannel_ChannelNotFound() {
        Long channelId = 1L;

        when(channelRepository.findById(eq(channelId)))
                .thenReturn(Mono.empty());

        Mono<Channel> result = channelService.getChannel(channelId);

        StepVerifier.create(result)
                .expectError(ChannelNotFoundException.class)
                .verify();
    }

    @Test
    void getCommunityChannels() {
        Long communityId = 1L;

        Channel channel1 = new Channel();
        Channel channel2 = new Channel();

        when(channelRepository.findChannelsByCommunityId(eq(communityId)))
                .thenReturn(Flux.just(channel1, channel2));

        Flux<ChannelDTO> result = channelService.getCommunityChannels(communityId);

        StepVerifier.create(result)
                .expectNext(channel1)
                .expectNext(channel2)
                .verifyComplete();
    }
}