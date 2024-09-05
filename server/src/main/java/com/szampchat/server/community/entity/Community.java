package com.szampchat.server.community.entity;

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
    private String imageUrl;
}