package com.szampchat.server.role;

import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.role.dto.RoleDTO;
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
                .map(Role::getCommunity)
                .flatMap(community -> communityMemberService.isMember(community, userId));
    }

    public Flux<Role> getMemberRoles(Long communityId, Long userId) {
        return roleRepository.findRolesByCommunityAndUser(communityId, userId);
    }

    public Mono<Role> findRole(Long roleId) {
        return roleRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new RoleNotFoundException()));
    }

    public Flux<RoleDTO> findRolesForCommunity(Long communityId) {
        return roleRepository.findRolesByCommunity(communityId)
                .map(this::toDto);
    }

    RoleDTO toDto(Role role) {
        return modelMapper.map(role, RoleDTO.class);
    }

//    public Mono<Role> save() {
//
//    }

//    public Flux<Role> groupUserRolesForCommunity(Long communityId) {
//        return
//    }
}
