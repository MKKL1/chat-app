package com.szampchat.server.role.repository;

import com.szampchat.server.role.dto.UserRolesDTO;
import com.szampchat.server.role.entity.UserRole;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface UserRoleRepository extends R2dbcRepository<UserRole, Long> {
    Flux<UserRole> findUserRolesByRoleId(Long roleId);
    Flux<Void> removeUserRolesByRoleIdAndUserId(Long roleId, Long userId);

    @Query("""
        SELECT ur.user_id, array_agg(ur.role_id) AS role_ids
        FROM user_roles ur
                 JOIN (
            SELECT id
            FROM roles
            WHERE community_id = :community_id
        ) r ON ur.role_id = r.id
        WHERE ur.user_id IN (:ids)
        GROUP BY ur.user_id;
""")
    Flux<UserRolesDTO> findUserRolesByUserIds(List<Long> ids, @Param("community_id") Long communityId);
}
