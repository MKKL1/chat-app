package com.szampchat.server.channel.entity;

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
@Table("channels")
public class Channel {
    @Id
    @Column("id")
    private Long id;

    @Column("name")
    private String name;

    @Column("community_id")
    private Long community;

    @Column("type")
    private Byte type;

    //TODO add channel type (requires custom converter)
}