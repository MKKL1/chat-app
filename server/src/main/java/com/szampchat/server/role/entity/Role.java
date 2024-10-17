package com.szampchat.server.role.entity;

import com.szampchat.server.permission.data.PermissionOverwrites;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("roles")
public class Role {
    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("permission")
    private PermissionOverwrites permission;

    @Column("community_id")
    private Long community;
}