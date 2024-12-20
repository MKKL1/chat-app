package com.szampchat.server.community.dto;

import com.szampchat.server.permission.data.Permissions;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommunityDTO {
    @Schema(example = "4501678724218880", description = "ID of community")
    private Long id;
    @Schema(example = "My community", description = "Name of community")
    private String name;
    @Schema(example = "29754971884879872", description = "ID of community's owner")
    private Long ownerId;
    @Schema(example = "img/myimage.png")
    private UUID imageUrl;
    private Permissions basePermissions;
}
