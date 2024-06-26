package com.szampchat.server.community.entity;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.snowflake.SnowflakeGenerator;
import com.szampchat.server.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="communities")
public class Community {
    @Id
    @GeneratedValue(generator = SnowflakeGenerator.GENERATOR_NAME)
    @GenericGenerator(name = SnowflakeGenerator.GENERATOR_NAME, type = SnowflakeGenerator.class)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column
    private String image_url;

    @OneToMany(mappedBy = "community")
    private Set<Role> roles;

    @OneToMany(mappedBy = "community")
    private Set<Channel> channels;

    @ManyToMany(targetEntity = User.class, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "community_members",
            joinColumns = { @JoinColumn(name = "community_id") },
            inverseJoinColumns = { @JoinColumn(name = "user_id") }
    )
    private Set<User> members = new HashSet<>();
}