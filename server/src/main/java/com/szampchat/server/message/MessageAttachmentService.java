package com.szampchat.server.message;

import com.szampchat.server.message.dto.MessageAttachmentDTO;
import com.szampchat.server.message.entity.MessageAttachment;
import com.szampchat.server.message.repository.MessageAttachmentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class MessageAttachmentService {

    private final MessageAttachmentRepository messageAttachmentRepository;

    public Flux<MessageAttachmentDTO> findMessageAttachments(Long messageId, Long channelId){
        return messageAttachmentRepository.findAllByMessageAndChannel(messageId, channelId);
    }

}
