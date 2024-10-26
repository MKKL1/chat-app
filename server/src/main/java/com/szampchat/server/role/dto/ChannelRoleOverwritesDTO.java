package com.szampchat.server.role.dto;

import com.szampchat.server.permission.data.PermissionOverwrites;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelRoleOverwritesDTO {
    private Long channelId;
    private List<ChannelRoleOverwriteDTO> overwrites;
}
