package com.szampchat.server.message.dto;

import lombok.Data;

@Data
public class MessageAttachmentDTO {
    private Long id;
    private Integer size;
    private String name;
}
