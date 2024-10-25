package com.szampchat.server.role.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.role.dto.*;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.entity.UserRole;
import com.szampchat.server.role.exception.InvalidMembersException;
import com.szampchat.server.role.exception.RoleNotFoundException;
import com.szampchat.server.role.repository.RoleRepository;
import com.szampchat.server.role.repository.UserRoleRepository;
import com.szampchat.server.shared.InvalidPatchException;
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

    public Mono<RoleWithMembersDTO> getRoleWithMembers(Long roleId) {
        Mono<Set<Long>> membersMono = userRoleRepository.findUserRolesByRoleId(roleId)
                .map(UserRole::getUserId)
                .collect(Collectors.toSet());

        Mono<RoleDTO> roleMono = getRole(roleId);

        return Mono.zip(roleMono, membersMono)
                .map(tuple -> {
                    RoleDTO roleDTO = tuple.getT1();
                    Set<Long> members = tuple.getT2();
                    return new RoleWithMembersDTO(roleDTO, members);
                });
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
    public Mono<RoleWithMembersDTO> create(RoleCreateRequest roleCreateRequest, Long communityId) {
        return roleRepository.save(Role.builder()
                    .name(roleCreateRequest.getName())
                    .permission(roleCreateRequest.getPermissionOverwrites())
                    .community(communityId)
                    .build())
                .flatMap(role -> createBulkUserRole(role.getId(), roleCreateRequest.getMembers())
                        .map(UserRoleDTO::getUserId)
                        .collect(Collectors.toSet())
                        .map(users -> new RoleWithMembersDTO(toDto(role), users)));
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

    @Transactional
    public Mono<RoleWithMembersDTO> update(Long roleId, JsonPatch jsonPatch) {
        return getRoleWithMembers(roleId)
                .flatMap(existingRoleWithMembers -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode roleWithMembersNode = objectMapper.convertValue(existingRoleWithMembers, JsonNode.class);

                    try {
                        JsonNode patchedNode = jsonPatch.apply(roleWithMembersNode);

                        RoleWithMembersDTO patchedRoleWithMembers = objectMapper.treeToValue(patchedNode, RoleWithMembersDTO.class);

                        //Make sure that community id is not updated
                        patchedRoleWithMembers.getRole().setCommunity(existingRoleWithMembers.getRole().getCommunity());

                        Mono<RoleDTO> updatedRoleMono = updateRoleEntityIfChanged(existingRoleWithMembers.getRole(), patchedRoleWithMembers.getRole());
                        Mono<Set<Long>> updatedMembersMono = updateRoleMembersIfChanged(roleId, existingRoleWithMembers.getMembers(), patchedRoleWithMembers.getMembers());
                        return Mono.zip(updatedRoleMono, updatedMembersMono)
                                .map(tuple -> new RoleWithMembersDTO(tuple.getT1(), tuple.getT2()));

                    } catch (JsonPatchException | JsonProcessingException e) {
                        return Mono.error(new InvalidPatchException("Failed to apply patch", e));
                    }
                });
    }

    private Mono<RoleDTO> updateRoleEntityIfChanged(RoleDTO existingRole, RoleDTO patchedRole) {
        if (!existingRole.equals(patchedRole)) {
            return roleRepository.save(toEntity(patchedRole)).map(this::toDto);
        }
        return Mono.just(existingRole);
    }

    // Updates role members if there are changes, and returns updated member list
    private Mono<Set<Long>> updateRoleMembersIfChanged(Long roleId, Set<Long> existingMembers, Set<Long> patchedMembers) {
        if (!existingMembers.equals(patchedMembers)) {
            return updateRoleMembers(roleId, patchedMembers);
        }
        return Mono.just(existingMembers);
    }

    private Mono<Set<Long>> updateRoleMembers(Long roleId, Set<Long> newMembers) {
        return userRoleRepository.findUserRolesByRoleId(roleId)
                .map(UserRole::getUserId)
                .collectList()
                .flatMap(existingMembers -> {
                    List<Long> toAdd = newMembers.stream()
                            .filter(member -> !existingMembers.contains(member))
                            .collect(Collectors.toList());

                    List<Long> toRemove = existingMembers.stream()
                            .filter(member -> !newMembers.contains(member))
                            .collect(Collectors.toList());

                    Flux<Void> removeFlux = Flux.fromIterable(toRemove)
                            .flatMap(user -> userRoleRepository.removeUserRolesByRoleIdAndUserId(roleId, user));
                    Flux<UserRoleDTO> addFlux = Flux.fromIterable(toAdd)
                            .flatMap(memberId -> createUserRole(new UserRoleDTO(roleId, memberId)));

                    return removeFlux.thenMany(addFlux).then(Mono.just(newMembers));
                });
    }

    private Role toEntity(RoleDTO roleDTO) {
        return modelMapper.map(roleDTO, Role.class);
    }

}
