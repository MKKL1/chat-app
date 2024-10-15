package com.szampchat.server.role.repository;

import com.szampchat.server.role.entity.ChannelRole;
import com.szampchat.server.shared.permission.PermissionOverrides;
import com.szampchat.server.shared.permission.Permissions;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ChannelRoleRepository extends R2dbcRepository<ChannelRoleRepository, Long> {
}
