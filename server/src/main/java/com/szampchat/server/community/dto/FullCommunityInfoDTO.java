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
    CommunityDTO community;
    List<Channel> channels; //TODO DTO
    List<CommunityMemberRolesDTO> members;
    List<Role> roles; //TODO DTO
}
