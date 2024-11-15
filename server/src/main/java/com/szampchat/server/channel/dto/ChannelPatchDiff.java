package com.szampchat.server.channel.dto;

import com.szampchat.server.role.dto.ChannelRoleOverwriteDTO;

import java.util.List;

public record ChannelPatchDiff(List<ChannelRoleOverwriteDTO> updated, List<ChannelRoleOverwriteDTO> added, List<ChannelRoleOverwriteDTO> removed) {
}
