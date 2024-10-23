package com.szampchat.server.channel.dto;

import com.szampchat.server.permission.data.PermissionOverwrites;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Data
public class ChannelRolesDTO {
    private ChannelDTO channel;
    private Map<Long, PermissionOverwrites> overwrites;
}
