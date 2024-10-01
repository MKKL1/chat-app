package com.szampchat.server.tools.populate;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.message.entity.Message;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class GenericCommunityGenData {
    private User owner;

    private long randomMembers = 0;
    /**
     * Provide saved only
     */
    private List<User> members;

    private long randomRoles = 0;
    /**
     * Provide saved only
     */
    private List<Role> roles;
    /**
     * How many roles each user should have
     */
    private int rolesPerUser = 1;

    private long randomChannels = 0;
    /**
     * Provide saved only
     */
    private List<Channel> channels;

    private long randomMessages = 0;
    /**
     * Provide saved only
     */
    private Map<Channel, List<Message>> messages;
}
