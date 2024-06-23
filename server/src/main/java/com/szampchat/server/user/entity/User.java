package com.szampchat.server.user.entity;

import com.szampchat.server.community.entity.Community;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.snowflake.SnowflakeGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(generator = SnowflakeGenerator.GENERATOR_NAME)
    //TODO GenericGenerator is deprecated, use IdGeneratorType instead, or even better have snowflake generator in postgres https://github.com/mausimag/pgflake
    @GenericGenerator(name = SnowflakeGenerator.GENERATOR_NAME, type = SnowflakeGenerator.class)
    private Long id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column
    private URL image_url;
    @Column
    private String description;

    @ManyToMany(mappedBy = "members", cascade = CascadeType.ALL)
    private Set<Community> communities = new HashSet<>();
    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    private Set<Role> roles = new HashSet<>();
}
