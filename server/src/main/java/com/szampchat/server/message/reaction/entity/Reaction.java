package com.szampchat.server.message.reaction.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("reactions")
public class Reaction {
    @Column("emoji")
    private Character emoji;

    @Column("user_id")
    private Long user;

    @Column("reaction_id")
    private Long id;

    @Column("message_id")
    private Long message;

    @Column("channel_id")
    private Long channel;

}
