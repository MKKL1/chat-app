package com.szampchat.server.message.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.szampchat.server.reaction.dto.ReactionDTO;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "4501678724218880", description = "ID of message")
    private Long id;
    @Schema(example = "4501678724218880", description = "ID of message's text channel")
    @JsonProperty("channelId")
    private Long channel;
    @Schema(example = "My message content", description = "Content of message")
    private String text;
    @Schema(example = "not sure", description = "When message was updated")
    private Instant updated_at;
    @Schema(example = "4501678724218880", description = "ID of user that sent message")
    @JsonProperty("userId")
    private Long user;
    @Schema(example = "4501678724218880", description = "ID of message which this message responds to")
    private Long respondsToMessage;
    private String gifLink;

    private List<MessageAttachmentDTO> attachments;
    private List<ReactionDTO> reactions;
}
