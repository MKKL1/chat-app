package com.szampchat.server.community.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommunityDTO {
    @Schema(example = "4501678724218880", description = "ID of community")
    private final Long id;
    @Schema(example = "My community")
    private final String name;
    @Schema(example = "29754971884879872", description = "ID of community's owner")
    private final Long ownerId;
    @Schema(example = "img/myimage.png")
    private final String imageUrl;
}
