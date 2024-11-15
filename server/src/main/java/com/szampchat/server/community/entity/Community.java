package com.szampchat.server.community.entity;

import com.szampchat.server.permission.data.Permissions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("communities")
public class Community {
    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("owner_id")
    private Long ownerId;

    @Column("image_url")
    private UUID imageUrl;

    @Column("base_permissions")
    private Permissions basePermissions;
}