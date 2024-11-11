package com.szampchat.server.reaction.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("reactions")
public class Reaction {
    @Column("emoji")
    private String emoji;

    @Column("user_id")
    private Long user;

    @Column("message_id")
    private Long message;

    @Column("channel_id")
    private Long channel;

}
