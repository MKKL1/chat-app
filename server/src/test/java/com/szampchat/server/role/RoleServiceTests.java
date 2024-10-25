package com.szampchat.server.role;

import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.dto.RoleNoCommunityDTO;
import com.szampchat.server.role.entity.Role;
import com.szampchat.server.role.exception.RoleNotFoundException;
import com.szampchat.server.role.repository.RoleRepository;
import com.szampchat.server.role.service.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class RoleServiceTests {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CommunityMemberService communityMemberService;

    @InjectMocks
    private RoleService roleService;

    @Test
    void hasAccessToRoleInfo_AccessGranted() {
        Long roleId = 1L;
        Long userId = 2L;
        Long communityId = 3L;

        Role role = new Role();
        role.setCommunity(communityId);

        when(roleRepository.findById(eq(roleId)))
                .thenReturn(Mono.just(role));

        when(communityMemberService.isMember(eq(communityId), eq(userId)))
                .thenReturn(Mono.just(true));

        Mono<Boolean> result = roleService.hasAccessToRoleInfo(roleId, userId);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }


    @Test
    void hasAccessToRoleInfo_AccessDenied() {
        Long roleId = 1L;
        Long userId = 2L;
        Long communityId = 3L;

        Role role = new Role();
        role.setCommunity(communityId);

        when(roleRepository.findById(eq(roleId)))
                .thenReturn(Mono.just(role));

        when(communityMemberService.isMember(eq(communityId), eq(userId)))
                .thenReturn(Mono.just(false));

        Mono<Boolean> result = roleService.hasAccessToRoleInfo(roleId, userId);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void findRole_RoleFound() {
        Long roleId = 1L;

        Role role = new Role();

        when(roleRepository.findById(eq(roleId)))
                .thenReturn(Mono.just(role));

        Mono<Role> result = roleService.getRole(roleId);

        StepVerifier.create(result)
                .expectNext(role)
                .verifyComplete();
    }

    @Test
    void findRole_RoleNotFound() {
        Long roleId = 1L;

        when(roleRepository.findById(eq(roleId)))
                .thenReturn(Mono.empty());

        Mono<Role> result = roleService.getRole(roleId);

        StepVerifier.create(result)
                .expectError(RoleNotFoundException.class)
                .verify();
    }

    @Test
    void findRolesForCommunity() {
        Long communityId = 1L;

        Role role1 = new Role();
        Role role2 = new Role();

        when(roleRepository.findRolesByCommunity(eq(communityId)))
                .thenReturn(Flux.just(role1, role2));

        Flux<RoleNoCommunityDTO> result = roleService.findRolesForCommunity(communityId);

        StepVerifier.create(result)
                .expectNext(role1)
                .expectNext(role2)
                .verifyComplete();
    }
}