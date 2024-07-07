package com.szampchat.server.community;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.repository.CommunityRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api/communities")
public class CommunityController {
    private final CommunityService communityService;

    @Operation(summary = "Get detailed info about community")
    @GetMapping("/{communityId}")
    public Mono<Community> getCommunity(@PathVariable Long communityId) {
        return communityService.findById(communityId);
    }
}
