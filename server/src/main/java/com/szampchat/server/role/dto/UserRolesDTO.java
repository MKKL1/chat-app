package com.szampchat.server.role.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRolesDTO {
    private Long userId;
    private Set<Long> roleIds;
}
