package com.szampchat.server.community;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.repository.CommunityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api/communities")
public class CommunityController {
    private final CommunityService communityService;
    private final ModelMapper modelMapper;

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
        return communityService.getCommunityMembers(communityId)
                .map(communityMember -> modelMapper.map(communityMember, CommunityMemberDTO.class));
    }
}
