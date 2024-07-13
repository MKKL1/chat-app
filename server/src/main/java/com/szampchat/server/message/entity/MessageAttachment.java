package com.szampchat.server.message.entity;

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
@Table("message_attachment")
public class MessageAttachment {
    @Id
    @Column("id")
    private Long id;

    @Column("path")
    private String path;

    @Column("size")
    private Integer size;

    @Column("name")
    private String name;

    @Column("message_id")
    private Long message;

    @Column("channel_id")
    private Long channel;
}
