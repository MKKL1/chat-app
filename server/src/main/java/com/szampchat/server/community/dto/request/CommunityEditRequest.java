package com.szampchat.server.community.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommunityEditRequest {
    private String name;
    private Integer basePermissions;
}
