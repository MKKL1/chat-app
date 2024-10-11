package com.szampchat.server.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class MessageAttachmentDTO {
    @Schema(example = "4501678724218880", description = "ID of attachment")
    private Long id;
    @Schema(example = "2048", description = "Size of file in bytes")
    private Integer size;
    @Schema(example = "myfile.txt", description = "ID of attachment")
    private String name;
}
