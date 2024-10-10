package com.szampchat.server.community.dto;

import com.szampchat.server.user.dto.UserDTO;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Builder
@Data
public class CommunityMemberRolesDTO {
    private final UserDTO user;
    @ArraySchema(minContains = 1,
            uniqueItems = true,
            schema = @Schema(description = "ID of role", example = "8518649245597696"),
            arraySchema = @Schema(description = "Roles of user in given community"))
    private final Set<Long> roles;
}
