package com.szampchat.server.community.service;

import com.szampchat.server.community.CommunityMemberRolesRow;
import com.szampchat.server.community.dto.CommunityMemberDTO;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.repository.CommunityMemberRepository;
import com.szampchat.server.user.dto.UserDTO;
import com.szampchat.server.user.entity.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Service
public class CommunityMemberService {
    private final CommunityMemberRepository communityMemberRepository;
    private final ModelMapper modelMapper;

    public Mono<Boolean> isMember(Long communityId, Long userId) {
        return communityMemberRepository.isMemberOfCommunity(communityId, userId);
    }

    public Flux<CommunityMemberDTO> getCommunityMembers(Long communityId) {
        return communityMemberRepository.fetchMemberWithRolesFromCommunity(communityId)
                //Grouping rows by user id
                .collectMultimap(
                        UserCompositeKey::new,
                        CommunityMemberRolesRow::getRole
                )
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .map(entry -> {
                    User user = entry.getKey().user();
                    Set<Long> roles = new HashSet<>(entry.getValue());
                    return CommunityMemberDTO.builder()
                            .user(modelMapper.map(user, UserDTO.class))
                            .roles(roles)
                            .build();
                });
    }

    public Mono<CommunityMember> create(Long communityId, Long userId) {
        return communityMemberRepository.save(CommunityMember.builder()
                        .communityId(communityId)
                        .userId(userId)
                .build());
    }

    //Helper class to identify User object by id instead of it's fields
    private record UserCompositeKey(User user) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UserCompositeKey that = (UserCompositeKey) o;
            return Objects.equals(user.getId(), that.user().getId());
        }

        @Override
        public int hashCode() {
            return Objects.hash(user.getId());
        }
    }
}
