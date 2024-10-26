package com.szampchat.server.role.service;

import com.szampchat.server.role.dto.ChannelRoleOverwriteDTO;
import com.szampchat.server.role.dto.ChannelRoleOverwritesDTO;
import com.szampchat.server.role.entity.ChannelRole;
import com.szampchat.server.role.repository.ChannelRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@AllArgsConstructor
@Service
public class ChannelRoleService {
    private final ChannelRoleRepository channelRoleRepository;

    public Flux<ChannelRoleOverwriteDTO> getChannelOverwrites(Long channelId) {
        return channelRoleRepository.findChannelRoleByChannelId(channelId)
                .map(cr -> new ChannelRoleOverwriteDTO(cr.getRoleId(), cr.getPermissionOverwrites()));
    }

    public Flux<ChannelRoleOverwritesDTO> getChannelOverwritesBulk(List<Long> channelIds) {
        return channelRoleRepository.findChannelRoleByChannelIdIn(channelIds)
                .groupBy(ChannelRole::getChannelId)
                .flatMap(group -> group
                        .map(cr -> new ChannelRoleOverwriteDTO(cr.getRoleId(), cr.getPermissionOverwrites()))
                        .collectList()
                        .map(overwrites -> new ChannelRoleOverwritesDTO(group.key(), overwrites))
                );
    }
}
