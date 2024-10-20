package com.szampchat.server.role.entity;

import com.szampchat.server.permission.data.PermissionOverwrites;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("channel_roles")
public class ChannelRole {
    @Column("role_id")
    private Long roleId;

    @Column("channel_id")
    private Long channelId;

    @Column("permission")
    private PermissionOverwrites permissionOverwrites;
}
