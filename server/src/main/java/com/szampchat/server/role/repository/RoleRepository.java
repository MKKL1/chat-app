package com.szampchat.server.role.repository;

import com.szampchat.server.role.entity.Role;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RoleRepository extends R2dbcRepository<Role, Long> {
    Flux<Role> findRolesByCommunity(Long community);
}
