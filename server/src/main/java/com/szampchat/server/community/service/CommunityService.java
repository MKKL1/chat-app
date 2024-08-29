package com.szampchat.server.community.service;

import com.szampchat.server.community.dto.CommunityCreateDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.exception.CommunityNotFoundException;
import com.szampchat.server.community.repository.CommunityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class CommunityService {
    private final CommunityRepository communityRepository;

    public Mono<Community> findById(Long id) {
        return communityRepository.findById(id)
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()));
    }

    public Flux<Community> getAllCommunities(){
        return communityRepository.findAll();
    }

    // Cannot create user
    public Mono<Community> save(Community community){
        return communityRepository.save(community)
                .switchIfEmpty(Mono.error(new Exception()));
    }
}
