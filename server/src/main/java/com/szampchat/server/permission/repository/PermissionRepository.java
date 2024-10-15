package com.szampchat.server.permission.repository;

import com.szampchat.server.shared.permission.PermissionOverrides;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

//Not sure if it should be repo
public interface PermissionRepository {
    Flux<PermissionOverrides> findPermissionsByChannelAndUser(Long channel_id, Long userId);
}
