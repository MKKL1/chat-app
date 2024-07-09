package com.szampchat.server.community;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.repository.CommunityRepository;
import com.szampchat.server.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Service
public class CommunityService {
    private final CommunityRepository communityRepository;

    public Mono<Community> findById(Long id) {
        return communityRepository.findById(id)
                .switchIfEmpty(Mono.error(new CommunityNotFoundException()));
    }

    public Flux<CommunityMember> getCommunityMembers(Long communityId) {
        return communityRepository.fetchMemberWithRolesFromCommunity(communityId)
                //Grouping rows by user id
                .collectMultimap(
                        UserCompositeKey::new,
                        CommunityMemberRolesRow::getRole
                )
                .flatMapMany(map -> Flux.fromIterable(map.entrySet()))
                .map(entry -> {
                    User user = entry.getKey().user();
                    Set<Long> roles = new HashSet<>(entry.getValue());
                    return CommunityMember.builder()
                            .user(user)
                            .roles(roles)
                            .build();
                });
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
