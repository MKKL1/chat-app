package com.szampchat.server.role.service;

import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.role.dto.RoleDTO;
import com.szampchat.server.role.dto.UserRoleDTO;
import com.szampchat.server.role.entity.UserRole;
import com.szampchat.server.role.exception.InvalidMembersException;
import com.szampchat.server.role.exception.UserNotMemberException;
import com.szampchat.server.role.repository.UserRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class UserRoleService {
    private final UserRoleRepository repository;
    private final CommunityMemberService communityMemberService;
    private final RoleService roleService;



    UserRoleDTO toDTO(UserRole userRole) {
        return new UserRoleDTO(userRole.getRoleId(), userRole.getUserId());
    }
}
