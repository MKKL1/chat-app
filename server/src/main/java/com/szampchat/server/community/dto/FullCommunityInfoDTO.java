package com.szampchat.server.community.dto;

import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.role.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FullCommunityInfoDTO {
    Community community;
    List<Channel> channels;
    List<CommunityMemberDTO> members;
    List<Role> roles;
}
