package com.szampchat.server.message.reaction;

import com.szampchat.server.channel.Channel;
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
@Table(name="reactions")
public class Reaction {
    @Id
    @GeneratedValue(generator = SnowflakeGenerator.GENERATOR_NAME)
    @GenericGenerator(name = SnowflakeGenerator.GENERATOR_NAME, type = SnowflakeGenerator.class)
    private Long id;

    @Column(nullable = false)
    private Character emoji;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "message_id", referencedColumnName = "message_id"),
            @JoinColumn(name = "channel_id", referencedColumnName = "channel_id")
    })
    private Message message;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;
}
