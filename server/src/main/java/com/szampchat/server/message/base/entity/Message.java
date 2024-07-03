package com.szampchat.server.message.base.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
@Data
@NoArgsConstructor
@Getter
@Setter
@Table(name = "messages")
public class Message {
    @Column("message_id")
    private Long id;

    @Column("channel_id")
    private Long channel;

    @Column("text")
    private String text;

    @Column("updated_at")
    private Instant updated_at;

    @Column("user_id")
    private Long user;

    @Column("responds_to_message_id")
    private Long respondsToMessage;
}
