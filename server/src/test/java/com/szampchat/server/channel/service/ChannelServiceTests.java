package com.szampchat.server.channel.service;

import com.szampchat.server.channel.dto.ChannelDTO;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.channel.exception.ChannelNotFoundException;
import com.szampchat.server.channel.repository.ChannelRepository;
import com.szampchat.server.community.service.CommunityMemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTests {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private CommunityMemberService communityMemberService;

    @Spy
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
        channel.setId(channelId);

        ChannelDTO channelDTO = new ChannelDTO();
        channelDTO.setId(channelId);

        when(channelRepository.findById(eq(channelId)))
                .thenReturn(Mono.just(channel));
        doReturn(channelDTO).when(channelService).toDTO(eq(channel));

        Mono<ChannelDTO> result = channelService.getChannel(channelId);

        StepVerifier.create(result)
                .expectNext(channelDTO)
                .verifyComplete();
    }

    @Test
    void getChannel_ChannelNotFound() {
        Long channelId = 1L;

        when(channelRepository.findById(eq(channelId)))
                .thenReturn(Mono.empty());

        Mono<ChannelDTO> result = channelService.getChannel(channelId);

        StepVerifier.create(result)
                .expectError(ChannelNotFoundException.class)
                .verify();
    }

    @Test
    void getCommunityChannels() {
        Long communityId = 1L;

        Channel channel1 = new Channel();
        channel1.setId(2L);
        Channel channel2 = new Channel();
        channel1.setId(3L);

        ChannelDTO channel1DTO = new ChannelDTO();
        channel1DTO.setId(2L);

        ChannelDTO channel2DTO = new ChannelDTO();
        channel2DTO.setId(3L);

        when(channelRepository.findChannelsByCommunityId(eq(communityId)))
                .thenReturn(Flux.just(channel1, channel2));
        doReturn(channel1DTO).when(channelService).toDTO(eq(channel1));
        doReturn(channel2DTO).when(channelService).toDTO(eq(channel2));

        Flux<ChannelDTO> result = channelService.getCommunityChannels(communityId);

        StepVerifier.create(result)
                .expectNext(channel1DTO)
                .expectNext(channel2DTO)
                .verifyComplete();
    }
}