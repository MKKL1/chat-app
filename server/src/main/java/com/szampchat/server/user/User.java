package com.szampchat.server.user;

import com.szampchat.server.snowflake.SnowflakeGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.IdGeneratorType;
import org.hibernate.annotations.Type;

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
    @Column
    private Long id;
    @Column
    private String name;
    @Column
    private String email;
    @Column
    private String password;
}
