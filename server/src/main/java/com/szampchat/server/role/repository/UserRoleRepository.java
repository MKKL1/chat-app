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
        SELECT user_id, array_agg(role_id) AS role_ids
        FROM user_roles
        WHERE user_id IN (:ids)
        GROUP BY user_id;
""")
    Flux<UserRolesDTO> findUserRolesByUserIds(List<Long> ids);
}
