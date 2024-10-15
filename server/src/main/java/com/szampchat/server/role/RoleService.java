package com.szampchat.server.role;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
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
    private final CommunityService communityService;
    private final ChannelService channelService;

    public Mono<Boolean> hasAccessToRoleInfo(Long roleId, Long userId) {
        return findRole(roleId)
                .map(Role::getCommunity)
                .flatMap(community -> communityMemberService.isMember(community, userId));
    }

    public Mono<Permissions> getUserPermissionsForCommunity(Long communityId, Long userId) {
        return communityService.findById(communityId)
                .map(CommunityDTO::getBasePermissions)//Get base permission
                .flatMap(basePermissions ->
                        roleRepository.findRolesByCommunityAndUser(communityId, userId)
                        .map(Role::getPermission)//Get all permission overrides
                        .map(permissionOverride -> permissionOverride.apply(basePermissions)) //Apply them one by one
                        .doOnNext(basePermissions::setPermissionData) //Set new permissions to for next iteration
                        .then(Mono.just(basePermissions)) //Return permission on finish
                );
    }

    public Mono<Permissions> getUserPermissionsForChannel(Long channelId, Long userId) {
        return channelService.getChannel(channelId)
                .flatMap(channel -> {
                   return getUserPermissionsForCommunity(channel.getCommunityId(), userId)
                           .flatMap(permissions ->
                                   roleRepository.findRolesByCommunityAndUser(channel.getCommunityId(), userId)

                           )
                });
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
