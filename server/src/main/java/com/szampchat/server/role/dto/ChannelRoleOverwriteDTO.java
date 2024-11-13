package com.szampchat.server.role.dto;

import com.szampchat.server.permission.data.PermissionOverwrites;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelRoleOverwriteDTO {
    private Long roleId;
    private Long overwrites;
}
