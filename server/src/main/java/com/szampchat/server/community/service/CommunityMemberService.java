package com.szampchat.server.community.service;

import com.szampchat.server.community.dto.CommunityMemberRolesDTO;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.repository.CommunityMemberRepository;
import com.szampchat.server.shared.CustomPrincipalProvider;
import com.szampchat.server.user.UserService;
import com.szampchat.server.user.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Service("communityMemberService")
public class CommunityMemberService {
    private final CommunityMemberRepository communityMemberRepository;
    private final CustomPrincipalProvider customPrincipalProvider;
    private final UserService userService;

    @Deprecated
    public Mono<Boolean> isMember(Long communityId, Long userId) {
        return communityMemberRepository.isMemberOfCommunity(communityId, userId);
    }

    public Mono<Boolean> isMember(Long communityId) {
        return customPrincipalProvider.getPrincipal()
                .flatMap(user -> isMember(communityId, user.getUserId()))
                .onErrorReturn(false);
    }

    @Deprecated
    public Mono<Boolean> isNotMember(Long communityId, Long userId) {
        return isMember(communityId, userId).map(val -> !val);
    }

    public Mono<Boolean> isNotMember(Long communityId) {
        return customPrincipalProvider.getPrincipal()
                .flatMap(user -> isNotMember(communityId, user.getUserId()))
                .onErrorReturn(false);
    }

    //Although it is named CommunityMemberDTO it is not meant to be converted directly to CommunityMember
    //TODO get user info from UserService, not from spaghetti database query
    public Flux<CommunityMemberRolesDTO> getCommunityMembersWithRoles(Long communityId) {
        return communityMemberRepository.findMemberWithRolesFromCommunity(communityId)
                .collectMultimap(
                        CommunityMemberRepository.CommunityMemberRolesRow::getUser,
                        CommunityMemberRepository.CommunityMemberRolesRow::getRole)
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .flatMap(entry -> {
                    //We are using userService, as this method can be cached
                    //TODO maybe something like getBulkUserDTO(List<Long> userIds) would fit here better
                    Long userId = entry.getKey();
                    if(userId == null) return Mono.empty();
                    return userService.findUserDTO(entry.getKey()).map(userDto -> {
                        Set<Long> roles = new HashSet<>(entry.getValue());
                        return CommunityMemberRolesDTO.builder()
                                .user(userDto)
                                .roles(roles)
                                .build();
                    });
                });
    }

    public Mono<CommunityMember> create(Long communityId, Long userId) {
        return communityMemberRepository.save(new CommunityMember(communityId, userId));
    }
}
