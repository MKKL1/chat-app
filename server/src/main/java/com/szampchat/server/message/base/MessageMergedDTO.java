package com.szampchat.server.message.base;

import com.szampchat.server.message.attachment.MessageAttachmentDTO;
import com.szampchat.server.message.attachment.entity.MessageAttachment;
import com.szampchat.server.message.base.entity.Message;
import com.szampchat.server.message.reaction.ReactionPreview;
import lombok.Data;

import java.util.List;

@Data
public class MessageMergedDTO {
    private Message message;
    private List<MessageAttachmentDTO> attachments;
    private List<ReactionPreview> reactions;
}
