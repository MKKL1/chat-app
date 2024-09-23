package com.szampchat.server.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private Long channelId;
    private String text;
    private Instant updated_at;
    private Long userId;
    private Long respondsToMessage;
    private String gifLink;

    private List<MessageAttachmentDTO> attachments;
    private List<ReactionPreviewDTO> reactions;
}
