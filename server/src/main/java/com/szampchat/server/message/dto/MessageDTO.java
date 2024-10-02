package com.szampchat.server.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("channelId")
    private Long channel;
    private String text;
    private Instant updated_at;
    @JsonProperty("userId")
    private Long user;
    private Long respondsToMessage;
    private String gifLink;

    private List<MessageAttachmentDTO> attachments;
    private List<ReactionPreviewDTO> reactions;
}
