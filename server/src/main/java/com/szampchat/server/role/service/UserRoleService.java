package com.szampchat.server.role.service;

import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.role.RoleMapper;
import com.szampchat.server.role.dto.UserRoleDTO;
import com.szampchat.server.role.dto.UserRolesDTO;
import com.szampchat.server.role.entity.UserRole;
import com.szampchat.server.role.exception.InvalidMembersException;
import com.szampchat.server.role.repository.UserRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserRoleService {
    private final CommunityMemberService communityMemberService;
    private final UserRoleRepository userRoleRepository;
    private final RoleMapper roleMapper;

    public Flux<UserRoleDTO> getRoleMembers(Long roleId) {
        return userRoleRepository.findUserRolesByRoleId(roleId)
                .map(roleMapper::toDto);
    }

    public Flux<UserRolesDTO> getMemberRoleIdsBulk(List<Long> userIds, Long communityId) {
        return userRoleRepository.findUserRolesByUserIds(userIds, communityId);
    }

    public Flux<Void> delete(Long roleId, Long userId) {
        return userRoleRepository.removeUserRolesByRoleIdAndUserId(roleId, userId);
    }

    public Mono<UserRoleDTO> createUserRole(Long communityId, UserRoleDTO userRoleDTO) {
        return createUserRoleFromCommunity(communityId, userRoleDTO)
                .switchIfEmpty(Mono.error(new InvalidMembersException(List.of(userRoleDTO.getUserId()))));
    }

    public Flux<UserRoleDTO> createBulkUserRole(Long roleId, Long communityId, Set<Long> members) {
        return Flux.fromIterable(members)
                    .flatMap(member -> createUserRoleFromCommunity(communityId, new UserRoleDTO(roleId, member))
                            .map(Optional::of)  // Wrap valid member in Optional
                            .switchIfEmpty(Mono.just(Optional.empty()))  // Handle invalid members as empty Optional
                            .map(optional -> new AbstractMap.SimpleEntry<>(member, optional))
                    )
                    //Collect list of valid and invalid members
                    .collectList()
                    .flatMapMany(results -> {
                        List<Long> invalidMembers = results.stream()
                                .filter(entry -> entry.getValue().isEmpty())
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());

                        if (!invalidMembers.isEmpty())
                            return Mono.error(new InvalidMembersException(invalidMembers));

                        //Return valid members on success
                        return Flux.fromStream(results.stream()
                                .filter(entry -> entry.getValue().isPresent())
                                .map(entry -> entry.getValue().get())
                        );
                    });
    }

    private Mono<UserRoleDTO> createUserRoleFromCommunity(Long communityId, UserRoleDTO userRoleDTO) {
        return communityMemberService.isMember(communityId, userRoleDTO.getUserId())
                .flatMap(isMember -> {
                    if (!isMember) {
                        return Mono.empty();
                    }

                    return userRoleRepository.save(UserRole.builder()
                            .userId(userRoleDTO.getUserId())
                            .roleId(userRoleDTO.getRoleId())
                            .build());
                })
                .map(userRole -> new UserRoleDTO(userRole.getRoleId(), userRole.getUserId()));
    }
}
