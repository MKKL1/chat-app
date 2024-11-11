package com.szampchat.server.community.service;

import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.exception.CommunityNotFoundException;
import com.szampchat.server.community.repository.CommunityRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class CommunityCacheService {
    private final CommunityRepository communityRepository;
    private final ModelMapper modelMapper;

    @Cacheable(value = "comm", key = "#id", cacheManager = "caffeineCacheManager")
    public Mono<CommunityDTO> findById(Long id) {
        return communityRepository.findById(id)
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()))
                .map(community -> modelMapper.map(community, CommunityDTO.class));
    }
}
