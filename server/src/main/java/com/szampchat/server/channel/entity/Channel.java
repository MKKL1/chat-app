package com.szampchat.server.channel.entity;

import com.szampchat.server.channel.ChannelType;
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
    private Long communityId;

    @Column("type")
    private ChannelType type;
}