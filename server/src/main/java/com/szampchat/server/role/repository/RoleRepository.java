package com.szampchat.server.role.repository;

import com.szampchat.server.role.entity.Role;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RoleRepository extends R2dbcRepository<Role, Long> {
    Flux<Role> findRolesByCommunity(Long community);

    @Query("""
        SELECT r.* FROM (SELECT *
                             FROM roles
                             WHERE roles.community_id = :community_id) as "r"
                          JOIN user_roles as ur on r.id = ur.role_id
        WHERE ur.user_id = :user_id""")
    Flux<Role> findRolesByCommunityAndUser(@Param("community_id") Long community_id, @Param("user_id") Long userId);
}
