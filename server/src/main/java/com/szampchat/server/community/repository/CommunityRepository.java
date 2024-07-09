package com.szampchat.server.community.repository;

import com.szampchat.server.community.CommunityMemberRolesRow;
import com.szampchat.server.community.entity.Community;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CommunityRepository extends R2dbcRepository<Community, Long> {

    /**
     * Fetch user data with it's corresponding role id.
     * Remember to map rows, as user data is repeated for each different role.
     * @param community Id of community
     */
    @Query("""
    SELECT u.*, ur.role_id as role
    FROM community_members as cm
    join user_roles ur on cm.user_id = ur.user_id
    join users u on cm.user_id = u.id
    join roles r on ur.role_id = r.id
    WHERE cm.community_id = :community AND r.community_id = cm.community_id""")
    Flux<CommunityMemberRolesRow> fetchMemberWithRolesFromCommunity(@Param("community") Long community);
}
