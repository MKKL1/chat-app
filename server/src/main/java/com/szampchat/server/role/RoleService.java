package com.szampchat.server.role;

import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.role.dto.RoleCreateDTO;
import com.szampchat.server.role.dto.RoleDTO;
import com.szampchat.server.role.dto.RoleNoCommunityDTO;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.exception.RoleNotFoundException;
import com.szampchat.server.role.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service("roleService")
public class RoleService {
    private final RoleRepository roleRepository;
    private final CommunityMemberService communityMemberService;
    private final ModelMapper modelMapper;

    public Mono<Boolean> hasAccessToRoleInfo(Long roleId, Long userId) {
        return findRole(roleId)
                .map(RoleDTO::getCommunity)
                .flatMap(community -> communityMemberService.isMember(community, userId));
    }

    public Flux<RoleNoCommunityDTO> getMemberRoles(Long communityId, Long userId) {
        return roleRepository.findRolesByCommunityAndUser(communityId, userId)
                .map(this::toNoCommunityDTO);
    }

    public Mono<RoleDTO> findRole(Long roleId) {
        return roleRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new RoleNotFoundException()))
                .map(this::toDto);
    }

    public Flux<RoleNoCommunityDTO> findRolesForCommunity(Long communityId) {
        return roleRepository.findRolesByCommunity(communityId)
                .map(this::toNoCommunityDTO);
    }

    RoleNoCommunityDTO toNoCommunityDTO(Role role) {
        return modelMapper.map(role, RoleNoCommunityDTO.class);
    }

    RoleDTO toDto(Role role) {
        return modelMapper.map(role, RoleDTO.class);
    }

    public Mono<RoleDTO> create(RoleCreateDTO roleCreateDTO) {
        return null;
    }

}
