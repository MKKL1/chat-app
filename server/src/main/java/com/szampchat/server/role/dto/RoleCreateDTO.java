package com.szampchat.server.role.dto;

import com.szampchat.server.permission.data.PermissionOverwrites;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleCreateDTO {
    private String name;
    private PermissionOverwrites permissionOverwrites;
    private Set<Long> members;
}
