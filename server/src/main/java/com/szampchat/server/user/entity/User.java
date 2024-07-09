package com.szampchat.server.user.entity;

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
@Table("users")
public class User {
    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("image_url")
    private String imageUrl;

    @Column("description")
    private String description;
}
