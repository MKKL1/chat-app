package com.szampchat.server.community.service;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.community.dto.CommunityCreateDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.exception.CommunityNotFoundException;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class CommunityService {
    private final CommunityRepository communityRepository;
    private final UserService userService;

    public Mono<Community> findById(Long id) {
        return communityRepository.findById(id)
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()));
    }

    public Flux<Community> getAllCommunities(){
        return communityRepository.findAll();
    }

    public Flux<Community> getUserCommunities(){
        return Flux.empty();
    }

    // Maybe also add owner as member of community?
    // Important - I need a way to return full User object, not just owner_id
    public Mono<Community> save(Community community, CurrentUser user){
        community.setOwner_id(user.getUserId());
        return communityRepository.save(community)
                .switchIfEmpty(Mono.error(new Exception()));
    }
}
