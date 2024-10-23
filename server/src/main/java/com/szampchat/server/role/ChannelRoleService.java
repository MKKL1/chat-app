package com.szampchat.server.role;

import com.szampchat.server.role.dto.ChannelRoleOverwriteDTO;
import com.szampchat.server.role.repository.ChannelRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@AllArgsConstructor
@Service
public class ChannelRoleService {
    private final ChannelRoleRepository channelRoleRepository;

    public Flux<ChannelRoleOverwriteDTO> getChannelOverwrites(Long channelId) {
        return channelRoleRepository.findChannelRoleByChannelId(channelId)
                .map(cr -> new ChannelRoleOverwriteDTO(cr.getRoleId(), cr.getPermissionOverwrites()));
    }
}
