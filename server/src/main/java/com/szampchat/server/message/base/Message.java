package com.szampchat.server.message.base;

import com.szampchat.server.channel.Channel;
import com.szampchat.server.message.attachment.MessageAttachment;
import com.szampchat.server.message.reaction.Reaction;
import com.szampchat.server.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name="messages")
public class Message {

    @EmbeddedId
    private MessageId id = new MessageId();

    @Column(nullable = false)
    private String text;

    @Column
    private Date updated_at;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

    //TODO do not set
    @MapsId("channelId")
    @ManyToOne
    @JoinColumn(name="channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name="responds_to_message_id", referencedColumnName="message_id"),
            @JoinColumn(name="responds_to_channel_id", referencedColumnName="channel_id")
    })
    private Message responds_to;

    @Builder
    public Message(String text, Date updated_at, User user, Channel channel, Message responds_to) {
        this.id = new MessageId(null, channel.getId());
        this.text = text;
        this.updated_at = updated_at;
        this.user = user;
        this.channel = channel;
        this.responds_to = responds_to;
    }

    @OneToMany(mappedBy="message")
    private Set<Reaction> reactions = new HashSet<>();

    @OneToMany(mappedBy="message")
    private Set<MessageAttachment> attachments = new HashSet<>();
}
