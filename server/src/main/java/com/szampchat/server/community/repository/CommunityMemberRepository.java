package com.szampchat.server.community.repository;

import com.szampchat.server.community.entity.CommunityMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommunityMemberRepository extends ReactiveCrudRepository<CommunityMember, Long> {

    //TODO return id's instead of whole user data
    /**
     * Fetch user data with it's corresponding role id.
     * Remember to map rows, as user data is repeated for each different role.
     * @param community ID of community
     */
    @Query("""
    SELECT cm.user_id as "user", ur.role_id as role
    FROM community_members as cm
    inner join user_roles ur on cm.user_id = ur.user_id
    inner join roles r on ur.role_id = r.id
    WHERE cm.community_id = :community AND r.community_id = cm.community_id""")
    Flux<CommunityMemberRolesRow> findMemberWithRolesFromCommunity(@Param("community") Long community);

    @Query("SELECT EXISTS (SELECT 1 FROM community_members WHERE community_id = :community AND user_id = :user)")
    Mono<Boolean> isMemberOfCommunity(@Param("community") Long community, @Param("user") Long user);

    //61096191995150336

    @Data
    @AllArgsConstructor
    class CommunityMemberRolesRow {
        Long user;
        Long role;
    }
}
