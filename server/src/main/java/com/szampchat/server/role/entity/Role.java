package com.szampchat.server.role.entity;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.snowflake.SnowflakeGenerator;
import com.szampchat.server.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="roles")
public class Role {
    @Id
    @GeneratedValue(generator = SnowflakeGenerator.GENERATOR_NAME)
    @GenericGenerator(name = SnowflakeGenerator.GENERATOR_NAME, type = SnowflakeGenerator.class)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Long permission;

    @ManyToOne
    @JoinColumn(name="community_id", nullable = false)
    private Community community;

    @ManyToMany(targetEntity = User.class, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = { @JoinColumn(name = "role_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> users = new HashSet<>();
}