package com.szampchat.server.role.repository;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRoleRepository extends R2dbcRepository<ChannelRoleRepository, Long> {
}
