package com.szampchat.server.community;

import com.szampchat.server.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

//won't be needed in near future
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class CommunityMemberRolesRow extends User {
    private Long role;
}
