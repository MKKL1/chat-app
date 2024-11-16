package com.szampchat.server.permission;

import com.szampchat.server.auth.CurrentUser;
import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.community.service.CommunityService;
import com.szampchat.server.permission.data.PermissionOverwrites;
import com.szampchat.server.permission.data.PermissionScope;
import com.szampchat.server.permission.repository.PermissionRepository;
import com.szampchat.server.role.dto.RoleDTO;
import com.szampchat.server.role.service.RoleService;
import com.szampchat.server.shared.CustomPrincipalProvider;
import com.szampchat.server.permission.data.PermissionFlag;
import com.szampchat.server.permission.data.Permissions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
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
        return communityService.getById(communityId)
                .flatMap(community -> {
                    //If user is owner, grant all permissions
                    if (community.getOwnerId().equals(userId)) return Mono.just(Permissions.allAllowed());

                    return Mono.just(community.getBasePermissions())
                            .flatMap(basePermissions ->
                                    getPermissionOverwrites(communityId, userId)
                                        .map(permissionOverwrites -> permissionOverwrites.applyToNew(basePermissions, PermissionScope.COMMUNITY))
                            );
                })
                .doOnNext(permissions -> log.info("User {} on community {} has perms {}", userId, communityId, permissions))
                //If role granted ADMIN flag, grant all permissions
                .map(permissions -> permissions.has(PermissionFlag.ADMINISTRATOR) ? Permissions.allAllowed() : permissions);
    }

    private Mono<PermissionOverwrites> getPermissionOverwrites(Long communityId, Long userId) {
        return roleService.getMemberRoles(communityId, userId)
                .map(RoleDTO::getPermissionOverwrites)
                .map(PermissionOverwrites::getPermissionOverwriteData)
                .reduce(0L, (acc, permissionData) -> acc | permissionData)
                .map(PermissionOverwrites::new);
    }

    public Mono<Permissions> getUserPermissionsForChannel(Long channelId, Long userId) {
        return channelService.getChannel(channelId)
                .flatMap(channel ->
                        getPermissionOverwrites(channel.getCommunityId(), userId)
                                .flatMap(communityPermissionOverwrites ->
                                    permissionRepository.findPermissionsByChannelAndUser(channelId, userId)
                                            .map(PermissionOverwrites::getPermissionOverwriteData)
                                            .reduce(0L, (acc, permissionData) -> acc | permissionData)
                                            .map(accumulatedPermissions -> new PermissionOverwrites(communityPermissionOverwrites.sum(accumulatedPermissions)))
                                )
                                .flatMap(channelsOverwrites -> communityService.getById(channel.getCommunityId())
                                        .flatMap(community -> {
                                            if (community.getOwnerId().equals(userId)) return Mono.just(Permissions.allAllowed());

                                            return Mono.just(channelsOverwrites.applyToNew(community.getBasePermissions(), PermissionScope.CHANNEL));
                                        })
                                )
                                .doOnNext(permissions -> log.info("User {} on channel {} has perms {}", userId, channelId, permissions))
                )
                .map(permissions -> permissions.has(PermissionFlag.ADMINISTRATOR) ? Permissions.allAllowed() : permissions);
    }

}
