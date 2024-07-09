package com.szampchat.server.message.attachment;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

@Data
public class MessageAttachmentDTO {
    private Long id;
    private Integer size;
    private String name;
}
