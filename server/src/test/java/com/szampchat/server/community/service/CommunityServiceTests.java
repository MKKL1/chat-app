package com.szampchat.server.community.service;

import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.exception.CommunityNotFoundException;
import com.szampchat.server.community.repository.CommunityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CommunityServiceTests {

    @Mock
    private CommunityRepository communityRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommunityService communityService;

    @Test
    void getById_CommunityFound() {
        Long communityId = 1L;
        Community community = new Community();
        community.setId(communityId);

        CommunityDTO communityDTO = new CommunityDTO();
        communityDTO.setId(communityId);

        when(communityRepository.findById(eq(communityId)))
                .thenReturn(Mono.just(community));
        when(modelMapper.map(community, CommunityDTO.class))
                .thenReturn(communityDTO);

        Mono<CommunityDTO> result = communityService.getById(communityId);

        StepVerifier.create(result)
                .expectNext(communityDTO)
                .verifyComplete();
    }

    @Test
    void getById_CommunityNotFound() {
        Long communityId = 1L;

        when(communityRepository.findById(eq(communityId)))
                .thenReturn(Mono.empty());

        Mono<CommunityDTO> result = communityService.getById(communityId);

        StepVerifier.create(result)
                .expectError(CommunityNotFoundException.class)
                .verify();
    }
}