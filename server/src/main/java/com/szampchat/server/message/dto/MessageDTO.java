package com.szampchat.server.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private Long channel;
    private String text;
    private Instant updated_at;
    private Long user;
    private Long respondsToMessage;

    private List<MessageAttachmentDTO> attachments;
    private List<ReactionPreviewDTO> reactions;
}
