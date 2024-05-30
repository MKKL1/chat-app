package com.szampchat.server.message;

import com.szampchat.server.channel.Channel;
import com.szampchat.server.channel.ChannelType;
import com.szampchat.server.community.Community;
import com.szampchat.server.message.attachment.MessageAttachment;
import com.szampchat.server.message.reaction.Reaction;
import com.szampchat.server.snowflake.SnowflakeGenerator;
import com.szampchat.server.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="messages")
public class Message {

    @EmbeddedId
    private MessageId id = new MessageId();

    @Column(nullable = false)
    private String text;

    @Column
    private Date updated_at;

//    @MapsId("channelId")
//    @ManyToOne
//    private Channel channel;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private User user;

//    @ManyToOne(cascade={CascadeType.ALL})
//    @JoinColumn(name="responds_to")
//    private Message responds_to;

//    @OneToMany(mappedBy="responds_to")
//    private Set<Message> responses = new HashSet<>();

    @OneToMany(mappedBy="message")
    private Set<Reaction> reactions = new HashSet<>();

    @OneToMany(mappedBy="message")
    private Set<MessageAttachment> attachments = new HashSet<>();
}
