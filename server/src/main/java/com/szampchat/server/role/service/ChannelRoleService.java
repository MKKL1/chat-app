package com.szampchat.server.role.service;

import com.szampchat.server.permission.data.PermissionScope;
import com.szampchat.server.role.RoleMapper;
import com.szampchat.server.role.dto.ChannelRoleOverwriteDTO;
import com.szampchat.server.role.dto.ChannelRoleOverwritesDTO;
import com.szampchat.server.role.entity.ChannelRole;
import com.szampchat.server.role.repository.ChannelRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
@Service
public class ChannelRoleService {
    private final ChannelRoleRepository channelRoleRepository;
    private final RoleMapper roleMapper;

    public Flux<ChannelRoleOverwriteDTO> getChannelOverwrites(Long channelId) {
        return channelRoleRepository.findChannelRoleByChannelId(channelId)
                .map(cr -> new ChannelRoleOverwriteDTO(cr.getRoleId(), cr.getPermissionOverwrites().validateAndGet(PermissionScope.CHANNEL)));
    }

    public Flux<ChannelRoleOverwritesDTO> getChannelOverwritesBulk(List<Long> channelIds) {
        return channelRoleRepository.findChannelRoleByChannelIdIn(channelIds)
                .groupBy(ChannelRole::getChannelId)
                .flatMap(group -> group
                        .map(cr -> new ChannelRoleOverwriteDTO(cr.getRoleId(), cr.getPermissionOverwrites().validateAndGet(PermissionScope.CHANNEL)))
                        .collectList()
                        .map(overwrites -> new ChannelRoleOverwritesDTO(group.key(), overwrites))
                );
    }

    //This is so baaaad
    public Flux<ChannelRoleOverwriteDTO> add(List<ChannelRoleOverwriteDTO> list, Long channelId) {
        return channelRoleRepository.saveAll(list.stream()
                .map(dto -> roleMapper.toEntity(dto, channelId))
                .toList())
                .map(roleMapper::toDTO);
    }

    public Flux<Void> update(List<ChannelRoleOverwriteDTO> list, Long channelId) {
        return Flux.fromStream(list.stream().map(dto -> roleMapper.toEntity(dto, channelId)))
                .flatMap(channelRole ->
                        channelRoleRepository.updateByChannelIdAndRoleId(
                                channelRole.getChannelId(),
                                channelRole.getRoleId(),
                                channelRole.getPermissionOverwrites().getPermissionOverwriteData())
                );
    }

    public Mono<Void> delete(List<ChannelRoleOverwriteDTO> list, Long channelId) {
        return Flux.fromIterable(list)
                .flatMap(dto -> channelRoleRepository.deleteByChannelIdAndRoleId(channelId, dto.getRoleId()))
                .then();

    }


}
