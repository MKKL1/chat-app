package com.szampchat.server.role.repository;

import com.szampchat.server.role.entity.UserRole;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserRoleRepository extends R2dbcRepository<UserRole, Long> {
    Flux<UserRole> findUserRolesByRoleId(Long roleId);
    Flux<Void> removeUserRolesByRoleIdAndUserId(Long roleId, Long userId);
}
