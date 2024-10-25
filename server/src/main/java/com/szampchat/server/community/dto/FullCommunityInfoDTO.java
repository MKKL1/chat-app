package com.szampchat.server.community.dto;

import com.szampchat.server.channel.dto.ChannelRolesDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FullCommunityInfoDTO {
    CommunityDTO community;
    List<ChannelRolesDTO> channels;
    List<CommunityMemberRolesDTO> members;
    List<RoleNoCommunityDTO> roles;
}
