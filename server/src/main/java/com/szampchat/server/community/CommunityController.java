package com.szampchat.server.community;

import com.szampchat.server.community.dto.CommunityMemberDTO;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/communities")
public class CommunityController {
    private final CommunityService communityService;
    private final CommunityMemberService communityMemberService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200"),
            @ApiResponse(responseCode = "404", description = "Community by given ID was not found", content = @Content),
    })
    @Operation(summary = "Get detailed info about community")
    @GetMapping("/{communityId}")
    public Mono<Community> getCommunity(@PathVariable Long communityId) {
        return communityService.findById(communityId);
    }

    @GetMapping("/{communityId}/members")
    public Flux<CommunityMemberDTO> getCommunityMembers(@PathVariable Long communityId) {
        return communityMemberService.getCommunityMembers(communityId);
    }

    //TODO
    @PostMapping("/{communityId}/join")
    public Mono<Void> joinCommunity(@PathVariable Long communityId) {
        return Mono.empty();
    }
}
