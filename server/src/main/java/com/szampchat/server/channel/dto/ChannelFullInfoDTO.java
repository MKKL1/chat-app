package com.szampchat.server.channel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.szampchat.server.role.dto.ChannelRoleOverwriteDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Data
@Builder
public class ChannelFullInfoDTO {
    private ChannelDTO channel;
    private List<ChannelRoleOverwriteDTO> overwrites;
    private Set<Long> participants;
}
