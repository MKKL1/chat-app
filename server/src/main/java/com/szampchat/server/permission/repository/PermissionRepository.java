package com.szampchat.server.permission.repository;

import com.szampchat.server.permission.data.PermissionOverrides;
import reactor.core.publisher.Flux;

//Not sure if it should be repo
public interface PermissionRepository {
    Flux<PermissionOverrides> findPermissionsByChannelAndUser(Long channel_id, Long userId);
}
