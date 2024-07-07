package com.szampchat.server.role;

import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public Mono<Role> findRole(Long roleId) {
        return roleRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new RoleNotFoundException()));
    }

    public Flux<Role> findRolesForCommunity(Long communityId) {
        return roleRepository.findRolesByCommunity(communityId);
    }
}
