package com.szampchat.server.role.entity;

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
@Table("user_roles")
public class UserRole {
    @Column("role_id")
    private Long roleId;

    @Column("user_id")
    private Long userId;
}
