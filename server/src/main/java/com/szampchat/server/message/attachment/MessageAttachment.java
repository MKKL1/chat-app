package com.szampchat.server.message.attachment;

import com.szampchat.server.message.Message;
import com.szampchat.server.snowflake.SnowflakeGenerator;
import com.szampchat.server.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="message_attachment")
public class MessageAttachment {
    @Id
    @GeneratedValue(generator = SnowflakeGenerator.GENERATOR_NAME)
    @GenericGenerator(name = SnowflakeGenerator.GENERATOR_NAME, type = SnowflakeGenerator.class)
    private Long id;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private Integer size;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "message_id", referencedColumnName = "message_id"),
            @JoinColumn(name = "channel_id", referencedColumnName = "channel_id")
    })
    private Message message;
}
