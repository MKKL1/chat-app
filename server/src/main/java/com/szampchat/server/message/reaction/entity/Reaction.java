package com.szampchat.server.message.reaction.entity;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.message.base.entity.Message;
import com.szampchat.server.message.reaction.ReactionId;
import com.szampchat.server.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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

    @Builder
    public Reaction(Character emoji, Channel channel, Message message, User user) {
        this.id = new ReactionId(null, channel.getId());
        this.emoji = emoji;
        this.channel = channel;
        this.message = message;
        this.user = user;
    }
}
