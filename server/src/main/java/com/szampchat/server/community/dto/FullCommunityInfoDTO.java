package com.szampchat.server.community.dto;

import com.szampchat.server.channel.dto.ChannelFullInfoDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FullCommunityInfoDTO {
    CommunityDTO community;
    List<ChannelFullInfoDTO> channels;
    List<CommunityMemberRolesDTO> members;
    List<RoleNoCommunityDTO> roles;
}
