package com.szampchat.server.message.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
@Data
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "messages")
public class Message {
    @Column("message_id")
    private Long id;

    @Column("channel_id")
    private Long channelId;

    @Column("text")
    private String text;

    @Column("updated_at")
    private Instant updated_at;

    @Column("user_id")
    private Long userId;

    @Column("responds_to_message_id")
    private Long respondsToMessage;

    @Column("gif_link")
    private String gifLink;
}
