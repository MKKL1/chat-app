package com.szampchat.server.role.service;

import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.role.dto.RoleCreateDTO;
import com.szampchat.server.role.dto.RoleDTO;
import com.szampchat.server.role.dto.RoleNoCommunityDTO;
import com.szampchat.server.role.dto.UserRoleDTO;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.entity.UserRole;
import com.szampchat.server.role.exception.InvalidMembersException;
import com.szampchat.server.role.exception.RoleNotFoundException;
import com.szampchat.server.role.repository.RoleRepository;
import com.szampchat.server.role.repository.UserRoleRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service("roleService")
public class RoleService {
    private final RoleRepository roleRepository;
    private final CommunityMemberService communityMemberService;
    private final ModelMapper modelMapper;
    private final UserRoleRepository userRoleRepository;

    public Mono<Boolean> hasAccessToRoleInfo(Long roleId, Long userId) {
        return getRole(roleId)
                .map(RoleDTO::getCommunity)
                .flatMap(community -> communityMemberService.isMember(community, userId));
    }

    public Flux<RoleNoCommunityDTO> getMemberRoles(Long communityId, Long userId) {
        return roleRepository.findRolesByCommunityAndUser(communityId, userId)
                .map(this::toNoCommunityDTO);
    }

    public Mono<RoleDTO> getRole(Long roleId) {
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

    @Transactional
    public Mono<RoleDTO> create(RoleCreateDTO roleCreateDTO, Long communityId) {
        return roleRepository.save(Role.builder()
                    .name(roleCreateDTO.getName())
                    .permission(roleCreateDTO.getPermissionOverwrites())
                    .community(communityId)
                    .build())
                .flatMap(role -> createBulkUserRole(role.getId(), roleCreateDTO.getMembers())
                        .then(Mono.just(role)))
                .map(this::toDto);
    }

    public Mono<UserRoleDTO> createUserRole(UserRoleDTO userRoleDTO) {
        return getRole(userRoleDTO.getRoleId())
                .flatMap(roleDTO -> createUserRoleFromCommunity(roleDTO, userRoleDTO))
                .switchIfEmpty(Mono.error(new InvalidMembersException(List.of(userRoleDTO.getUserId()))));
    }

    public Flux<UserRoleDTO> createBulkUserRole(Long roleId, Set<Long> members) {
        return getRole(roleId)
                .flatMapMany(roleDTO ->
                        Flux.fromIterable(members)
                        .flatMap(member -> createUserRoleFromCommunity(roleDTO, new UserRoleDTO(roleId, member))
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
                                            .map(entry -> entry.getValue().get()
                                            )
                            );
                        })
                );
    }

    private Mono<UserRoleDTO> createUserRoleFromCommunity(RoleDTO roleDTO, UserRoleDTO userRoleDTO) {
        return communityMemberService.isMember(roleDTO.getCommunity(), userRoleDTO.getUserId())
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
