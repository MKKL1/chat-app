package com.szampchat.server.community.dto;

import com.szampchat.server.channel.dto.ChannelRolesDTO;
import com.szampchat.server.channel.entity.Channel;
import com.szampchat.server.community.entity.Community;
import com.szampchat.server.role.dto.RoleDTO;
import com.szampchat.server.role.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FullCommunityInfoDTO {
    CommunityDTO community;
    List<ChannelRolesDTO> channels;
    List<CommunityMemberRolesDTO> members;
    List<RoleDTO> roles; //TODO DTO
}
