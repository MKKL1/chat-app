package com.szampchat.server.permission.repository;

import com.szampchat.server.permission.data.PermissionOverwrites;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@Repository
public class PermissionRepositoryImpl implements PermissionRepository{
    private final DatabaseClient databaseClient;

    @Override
    public Flux<PermissionOverwrites> findPermissionsByChannelAndUser(Long channelId, Long userId) {
        return databaseClient.sql("""
            SELECT cr.permission FROM channel_roles as cr
            JOIN user_roles as ur on ur.role_id = cr.role_id
            WHERE cr.channel_id = :channelId AND ur.user_id = :userId
        """)
                .bind("channelId", channelId)
                .bind("userId", userId)
                .map((row, _) -> new PermissionOverwrites(row.get("permission", Long.class)))
                .all();
    }
}
