package com.szampchat.server.community.repository;

import com.szampchat.server.community.CommunityMemberRolesRow;
import com.szampchat.server.community.entity.CommunityMember;
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
    SELECT u.*, ur.role_id as role
    FROM community_members as cm
    join user_roles ur on cm.user_id = ur.user_id
    join users u on cm.user_id = u.id
    join roles r on ur.role_id = r.id
    WHERE cm.community_id = :community AND r.community_id = cm.community_id""")
    Flux<CommunityMemberRolesRow> fetchMemberWithRolesFromCommunity(@Param("community") Long community);

    @Query("SELECT EXISTS (SELECT 1 FROM community_members WHERE community_id = :community AND user_id = :user)")
    Mono<Boolean> isMemberOfCommunity(@Param("community") Long community, @Param("user") Long user);
}
