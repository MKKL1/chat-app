package com.szampchat.server.role.dto;

import com.szampchat.server.permission.data.PermissionOverrides;

public class RoleDTO {
    private Long id;
    private String name;
    private PermissionOverrides permissionOverrides;
    private Long community;
}
