package com.szampchat.server.tools.populate;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.community.entity.CommunityMember;
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
public class CommunityData {
    private final Community community;
    private final User owner;
    private final List<User> members;
    private final List<Role> roles;
    private final List<Channel> channels;
    private final List<Message> messages;
}
