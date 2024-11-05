package com.szampchat.server.role.dto.request;

import com.szampchat.server.permission.data.PermissionOverwrites;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleCreateRequest {
    @NotBlank(message = "Role name cannot be blank")
    @Size(max = 64, message = "Role name cannot exceed 64 characters")
    private String name;
    @NotBlank(message = "Permission overwrites cannot be blank")
    private Long permissionOverwrites;
    private Set<Long> members;
}
