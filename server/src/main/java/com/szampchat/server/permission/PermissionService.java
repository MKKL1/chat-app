package com.szampchat.server.permission;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.permission.data.PermissionContext;
import com.szampchat.server.permission.repository.PermissionRepository;
import com.szampchat.server.role.RoleService;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.shared.CustomPrincipalProvider;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.permission.data.Permissions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service("permissionService")
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final ChannelService channelService;
    private final CommunityService communityService;
    private final RoleService roleService;
    private final CustomPrincipalProvider customPrincipalProvider;

    //TODO can be simplified
    public Mono<Boolean> hasPermissionInCommunity(Long communityId, int permissionMask) {
        return customPrincipalProvider.getPrincipal()
                .map(CurrentUser::getUserId)
                .flatMap(userId -> getUserPermissionsForCommunity(communityId, userId))
                .map(permissions -> permissions.has(permissionMask));
    }

    public Mono<Boolean> hasPermissionInCommunity(Long communityId, PermissionFlag... permissionFlags) {
        return customPrincipalProvider.getPrincipal()
                .map(CurrentUser::getUserId)
                .flatMap(userId -> getUserPermissionsForCommunity(communityId, userId))
                .map(permissions -> permissions.has(permissionFlags));
    }

    public Mono<Boolean> hasPermissionInChannel(Long channelId, int permissionMask) {
        return customPrincipalProvider.getPrincipal()
                .map(CurrentUser::getUserId)
                .flatMap(userId -> getUserPermissionsForChannel(channelId, userId))
                .map(permissions -> permissions.has(permissionMask));
    }

    public Mono<Boolean> hasPermissionInChannel(Long channelId, PermissionFlag... permissionFlags) {
        return customPrincipalProvider.getPrincipal()
                .map(CurrentUser::getUserId)
                .flatMap(userId -> getUserPermissionsForChannel(channelId, userId))
                .map(permissions -> permissions.has(permissionFlags));
    }



    public Mono<Permissions> getUserPermissionsForCommunity(Long communityId, Long userId) {
        return communityService.findById(communityId)
                .map(CommunityDTO::getBasePermissions)//Get base permission
                .flatMap(basePermissions ->
                        roleService.getMemberRoles(communityId, userId)
                                .map(Role::getPermission)//Get all permission overrides
                                .map(permissionOverride -> permissionOverride.apply(basePermissions)) //Apply them one by one
                                .doOnNext(basePermissions::setPermissionData) //Set new permissions to for next iteration
                                .then(Mono.just(basePermissions)) //Return permission on finish
                );
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
