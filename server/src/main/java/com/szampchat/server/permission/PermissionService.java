package com.szampchat.server.permission;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.permission.repository.PermissionRepository;
import com.szampchat.server.role.dto.RoleDTO;
import com.szampchat.server.role.dto.RoleNoCommunityDTO;
import com.szampchat.server.role.service.RoleService;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.shared.CustomPrincipalProvider;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.permission.data.Permissions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@AllArgsConstructor
@Service("permissionService")
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final ChannelService channelService;
    private final CommunityService communityService;
    private final RoleService roleService;
    private final CustomPrincipalProvider customPrincipalProvider;

    public Mono<Boolean> hasPermissionInCommunity(Long communityId, int permissionMask) {
        return hasPermissionIn(userId -> getUserPermissionsForCommunity(communityId, userId), permissionMask);
    }

    public Mono<Boolean> hasPermissionInCommunity(Long communityId, PermissionFlag... permissionFlags) {
        return hasPermissionInCommunity(communityId, PermissionFlag.combineFlags(permissionFlags));
    }

    public Mono<Boolean> hasPermissionInChannel(Long channelId, int permissionMask) {
        return hasPermissionIn(userId -> getUserPermissionsForChannel(channelId, userId), permissionMask);
    }

    public Mono<Boolean> hasPermissionInChannel(Long channelId, PermissionFlag... permissionFlags) {
        return hasPermissionInChannel(channelId, PermissionFlag.combineFlags(permissionFlags));
    }

    private Mono<Boolean> hasPermissionIn(Function<Long, Mono<Permissions>> permissionCheckFunc, int permissionMask) {
        return customPrincipalProvider.getPrincipal()
                .map(CurrentUser::getUserId)
                .flatMap(permissionCheckFunc)
                .map(permissions -> permissions.has(permissionMask));
    }



    public Mono<Permissions> getUserPermissionsForCommunity(Long communityId, Long userId) {
        return communityService.findById(communityId)
                .flatMap(community -> {
                    //If user is owner, grant all permissions
                    if (community.getOwnerId().equals(userId)) return Mono.just(Permissions.allAllowed());

                    return Mono.just(community.getBasePermissions())
                            .flatMap(basePermissions ->
                                    roleService.getMemberRoles(communityId, userId)
                                            .map(RoleNoCommunityDTO::getPermissionOverwrites) // Get all permission overrides
                                            .map(permissionOverride -> permissionOverride.apply(basePermissions)) // Apply them one by one
                                            .doOnNext(basePermissions::setPermissionData) // Set new permissions for next iteration
                                            .then(Mono.just(basePermissions)) // Return permission on finish
                            );
                })
                //If role granted ADMIN flag, grant all permissions
                .map(permissions -> permissions.has(PermissionFlag.ADMINISTRATOR) ? Permissions.allAllowed() : permissions);
    }

    public Mono<Permissions> getUserPermissionsForChannel(Long channelId, Long userId) {
        return channelService.getChannel(channelId)
                .flatMap(channel ->
                        getUserPermissionsForCommunity(channel.getCommunityId(), userId)
                                .flatMap(basePermissions ->
                                        permissionRepository.findPermissionsByChannelAndUser(channelId, userId)
                                                .map(permissionOverride -> permissionOverride.apply(basePermissions))
                                                .doOnNext(basePermissions::setPermissionData)
                                                .then(Mono.just(basePermissions))

                                ));
    }

}
