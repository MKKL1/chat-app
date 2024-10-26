package com.szampchat.server.role.dto;

import com.szampchat.server.permission.data.PermissionOverwrites;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChannelRoleOverwriteDTO {
    private final Long roleId;
    private final PermissionOverwrites overwrites;
}
