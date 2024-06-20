package com.szampchat.server.message.reaction;

import com.szampchat.server.channel.Channel;
import com.szampchat.server.message.base.Message;
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
    @EmbeddedId
    private ReactionId id = new ReactionId();

    @Column(nullable = false)
    private Character emoji;

    @ManyToOne
    @MapsId("channelId")
    @JoinColumn(name = "channel_id", insertable = false, updatable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "message_id", referencedColumnName = "message_id"),
            @JoinColumn(name = "message_channel_id", referencedColumnName = "channel_id")
    })
    private Message message;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;
}
