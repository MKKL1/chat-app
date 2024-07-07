package com.szampchat.server.community;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.repository.CommunityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class CommunityService {
    private final CommunityRepository communityRepository;

    public Mono<Community> findById(Long id) {
        return communityRepository.findById(id);
    }
}
