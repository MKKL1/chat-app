package com.szampchat.server.message.base;

import com.szampchat.server.message.attachment.entity.MessageAttachment;
import com.szampchat.server.message.base.entity.Message;
import com.szampchat.server.message.reaction.ReactionPreview;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class MessageMergedData {
    private Message message;
    private List<MessageAttachment> attachments;
    private List<ReactionPreview> reactions;
}
