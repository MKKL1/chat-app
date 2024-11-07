package com.szampchat.server.role.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.permission.data.PermissionOverwrites;
import com.szampchat.server.role.RoleMapper;
import com.szampchat.server.role.dto.*;
import com.szampchat.server.role.dto.request.RoleCreateRequest;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.event.RoleCreateEvent;
import com.szampchat.server.role.event.RoleDeleteEvent;
import com.szampchat.server.role.event.RoleUpdateEvent;
import com.szampchat.server.role.exception.RoleNotFoundException;
import com.szampchat.server.role.repository.RoleRepository;
import com.szampchat.server.shared.InvalidPatchException;
import lombok.AllArgsConstructor;
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
    private final RoleMapper roleMapper;
    private final UserRoleService userRoleService;
    private final EventSink eventSink;

    public Mono<Boolean> hasAccessToRoleInfo(Long roleId, Long userId) {
        return getRole(roleId)
                .map(RoleDTO::getCommunity)
                .flatMap(community -> communityMemberService.isMember(community, userId));
    }

    public Flux<RoleDTO> getMemberRoles(Long communityId, Long userId) {
        return roleRepository.findRolesByCommunityAndUser(communityId, userId)
                .map(roleMapper::toDto);
    }

    public Mono<RoleDTO> getRole(Long roleId) {
        return roleRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new RoleNotFoundException(roleId)))
                .map(roleMapper::toDto);
    }

    public Flux<RoleDTO> getRolesByCommunity(Long communityId) {
        return roleRepository.findRolesByCommunity(communityId)
                .map(roleMapper::toDto);
    }

    public Mono<RoleWithMembersDTO> getRoleWithMembers(Long roleId) {
        Mono<Set<Long>> membersMono = userRoleService.getRoleMembers(roleId)
                .map(UserRoleDTO::getUserId)
                .collect(Collectors.toSet());

        Mono<RoleDTO> roleMono = getRole(roleId);

        return Mono.zip(roleMono, membersMono)
                .map(tuple -> new RoleWithMembersDTO(tuple.getT1(), tuple.getT2()));
    }

    @Transactional
    public Mono<RoleWithMembersDTO> create(RoleCreateRequest roleCreateRequest, Long communityId) {
        return roleRepository.save(Role.builder()
                    .name(roleCreateRequest.getName())
                    .permission(new PermissionOverwrites(roleCreateRequest.getPermissionOverwrites()))
                    .community(communityId)
                    .build())
                //Save role users
                .flatMap(role -> userRoleService.createBulkUserRole(role.getId(), role.getCommunity(), roleCreateRequest.getMembers())
                        .map(UserRoleDTO::getUserId)
                        .collect(Collectors.toSet())
                        .map(users -> new RoleWithMembersDTO(roleMapper.toDto(role), users)))
                //Publish RoleCreateEvent
                .doOnNext(roleWithMembersDTO -> eventSink.publish(RoleCreateEvent.builder()
                                .recipient(Recipient.builder()
                                        .context(Recipient.Context.COMMUNITY)
                                        .id(roleWithMembersDTO.getRole().getCommunity())
                                        .build())
                                .data(roleWithMembersDTO)
                        .build()));
    }

    @Transactional
    public Mono<RoleWithMembersDTO> update(Long roleId, JsonPatch jsonPatch) {
        return getRoleWithMembers(roleId)
                .flatMap(existingRoleWithMembers -> {
                    RoleWithMembersDTO patchedRoleWithMembers = patch(existingRoleWithMembers, jsonPatch);

                    Mono<RoleDTO> updatedRoleMono = updateRoleEntityIfChanged(
                            existingRoleWithMembers.getRole(),
                            patchedRoleWithMembers.getRole()
                    );

                    Mono<Set<Long>> updatedMembersMono = updateRoleMembersIfChanged(
                            roleId,
                            existingRoleWithMembers.getRole().getCommunity(),
                            existingRoleWithMembers.getMembers(),
                            patchedRoleWithMembers.getMembers()
                    );

                    return Mono.zip(updatedRoleMono, updatedMembersMono)
                            .map(tuple -> new RoleWithMembersDTO(tuple.getT1(), tuple.getT2()));
                })
                .doOnNext(roleWithMembersDTO -> eventSink.publish(RoleUpdateEvent.builder()
                        .recipient(Recipient.builder()
                                .context(Recipient.Context.COMMUNITY)
                                .id(roleWithMembersDTO.getRole().getCommunity())
                                .build())
                        .data(roleWithMembersDTO)
                        .build()));
    }

    public Mono<Void> delete(Long roleId) {
        return getRole(roleId).flatMap(roleDTO ->
                    roleRepository.removeRoleById(roleId)
                    .doOnNext(_ -> eventSink.publish(RoleDeleteEvent.builder()
                                    .recipient(Recipient.builder()
                                            .context(Recipient.Context.COMMUNITY)
                                            .id(roleDTO.getCommunity())
                                            .build())
                                    .data(new RoleDeleteEvent.EventPayload(roleId))
                            .build()))
                )
                .then();
    }

    private RoleWithMembersDTO patch(RoleWithMembersDTO existingRoleWithMembers, JsonPatch jsonPatch) throws InvalidPatchException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode roleWithMembersNode = objectMapper.convertValue(existingRoleWithMembers, JsonNode.class);

        try {
            JsonNode patchedNode = jsonPatch.apply(roleWithMembersNode);
            RoleWithMembersDTO patchedRoleWithMembers = objectMapper.treeToValue(patchedNode, RoleWithMembersDTO.class);

            long communityId = existingRoleWithMembers.getRole().getCommunity();
            //Make sure that community id is not updated
            patchedRoleWithMembers.getRole().setCommunity(communityId);

            return patchedRoleWithMembers;
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new InvalidPatchException("Failed to apply patch", e);
        }
    }

    private Mono<RoleDTO> updateRoleEntityIfChanged(RoleDTO existingRole, RoleDTO patchedRole) {
        if (!existingRole.equals(patchedRole)) {
            return roleRepository.save(roleMapper.toEntity(patchedRole)).map(roleMapper::toDto);
        }
        return Mono.just(existingRole);
    }

    private Mono<Set<Long>> updateRoleMembersIfChanged(Long roleId, Long communityId, Set<Long> existingMembers, Set<Long> patchedMembers) {
        if (!existingMembers.equals(patchedMembers)) {
            return updateRoleMembers(roleId, communityId, patchedMembers);
        }
        return Mono.just(existingMembers);
    }

    private Mono<Set<Long>> updateRoleMembers(Long roleId, Long communityId, Set<Long> newMembers) {
        return userRoleService.getRoleMembers(roleId)
                .map(UserRoleDTO::getUserId)
                .collectList()
                .flatMap(existingMembers -> {
                    List<Long> toAdd = newMembers.stream()
                            .filter(member -> !existingMembers.contains(member))
                            .collect(Collectors.toList());

                    List<Long> toRemove = existingMembers.stream()
                            .filter(member -> !newMembers.contains(member))
                            .collect(Collectors.toList());

                    Flux<Void> removeFlux = Flux.fromIterable(toRemove)
                            .flatMap(user -> userRoleService.delete(roleId, user));
                    Flux<UserRoleDTO> addFlux = Flux.fromIterable(toAdd)
                            .flatMap(memberId -> userRoleService.createUserRole(communityId, new UserRoleDTO(roleId, memberId)));

                    return removeFlux
                            .thenMany(addFlux)
                            .then(Mono.just(newMembers));
                });
    }
}
