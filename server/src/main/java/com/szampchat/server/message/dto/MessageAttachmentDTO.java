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
    // I added this because it should be attached to message while publishing it to event sender
    // so user can instantly load attached file
    @Schema(example= "uploads/messages/random_file_name.png", description = "Path to file")
    private String path;
}
