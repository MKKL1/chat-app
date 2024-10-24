package com.szampchat.server.role.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDTO {
    private Long roleId;
    private Long userId;
}
