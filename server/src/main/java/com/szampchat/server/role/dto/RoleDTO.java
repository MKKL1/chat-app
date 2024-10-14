package com.szampchat.server.role.dto;

import com.szampchat.server.shared.permission.PermissionOverrides;

public class RoleDTO {
    private Long id;
    private String name;
    private PermissionOverrides permissionOverrides;
    private Long community;
}
