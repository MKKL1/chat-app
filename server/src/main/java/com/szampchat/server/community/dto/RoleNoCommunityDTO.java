package com.szampchat.server.community.dto;

import com.szampchat.server.permission.data.PermissionOverwrites;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleNoCommunityDTO {
    private Long id;
    private String name;
    private PermissionOverwrites permissionOverwrites;
//Removed community id, as all endpoints require to give community id anyway
    //    private Long community;
}
