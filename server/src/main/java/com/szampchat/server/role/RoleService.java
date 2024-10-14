package com.szampchat.server.role;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.exception.RoleNotFoundException;
import com.szampchat.server.role.repository.RoleRepository;
import com.szampchat.server.shared.permission.Permissions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service("roleService")
public class RoleService {
    private final RoleRepository roleRepository;
    private final CommunityMemberService communityMemberService;

    public Mono<Boolean> hasAccessToRoleInfo(Long roleId, Long userId) {
        return findRole(roleId)
                .map(Role::getCommunity)
                .flatMap(community -> communityMemberService.isMember(community, userId));
    }

    public Mono<Permissions> getPermissionsForCommunity(Long communityId, Long userId) {
        return roleRepository.findRolesByUser(communityId)
    }

    public Mono<Permissions> getPermissionsForChannel() {

    }

    public Flux<Role> getUserRolesForCommunity() {

    }

    public Mono<Role> findRole(Long roleId) {
        return roleRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new RoleNotFoundException()));
    }

    public Flux<Role> findRolesForCommunity(Long communityId) {
        return roleRepository.findRolesByCommunity(communityId);
    }

//    public Mono<Role> save() {
//
//    }

//    public Flux<Role> groupUserRolesForCommunity(Long communityId) {
//        return
//    }
}
