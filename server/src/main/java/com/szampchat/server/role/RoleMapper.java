package com.szampchat.server.role;

import com.szampchat.server.community.dto.RoleNoCommunityDTO;
import com.szampchat.server.permission.data.PermissionOverwrites;
import com.szampchat.server.role.dto.ChannelRoleOverwriteDTO;
import com.szampchat.server.role.dto.RoleDTO;
import com.szampchat.server.role.dto.UserRoleDTO;
import com.szampchat.server.role.entity.ChannelRole;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.entity.UserRole;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class RoleMapper {
    private final ModelMapper modelMapper;

    public RoleDTO toDto(Role role) {
        return modelMapper.map(role, RoleDTO.class);
    }

    public Role toEntity(RoleDTO roleDTO) {
        return modelMapper.map(roleDTO, Role.class);
    }

    public RoleNoCommunityDTO toNoCommunityDTO(Role role) {
        return modelMapper.map(role, RoleNoCommunityDTO.class);
    }

    public UserRoleDTO toDto(UserRole userRole) {
        return modelMapper.map(userRole, UserRoleDTO.class);
    }

    public ChannelRole toEntity(ChannelRoleOverwriteDTO dto, Long channelId) {
        return ChannelRole.builder()
                .roleId(dto.getRoleId())
                .channelId(channelId)
                .permissionOverwrites(new PermissionOverwrites(dto.getOverwrites()))
                .build();
    }

    public ChannelRoleOverwriteDTO toDTO(ChannelRole channelRole) {
        return ChannelRoleOverwriteDTO.builder()
                .roleId(channelRole.getRoleId())
                .overwrites(channelRole.getPermissionOverwrites().getPermissionOverwriteData())
                .build();
    }
}
