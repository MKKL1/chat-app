package com.szampchat.server.role.repository;

import com.szampchat.server.role.entity.ChannelRole;
import com.szampchat.server.role.entity.ChannelRoleId;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface ChannelRoleRepository extends R2dbcRepository<ChannelRole, ChannelRoleId> {
    Flux<ChannelRole> findChannelRoleByChannelId(Long channelId);
    Flux<ChannelRole> findChannelRoleByChannelIdIn(List<Long> ids);
    @Query("""
        UPDATE channel_roles
        SET permission = :permission
        WHERE channel_id = :channelId AND role_id = :roleId
""")
    Mono<Void> updateByChannelIdAndRoleId(@Param("channelId") Long channelId,
                                          @Param("roleId") Long roleId,
                                          @Param("permission") Long permission);

    Mono<Void> deleteByChannelIdAndRoleId(Long channelId, Long roleId);
}
