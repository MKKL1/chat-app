package com.szampchat.server.message.attachment.repository;

import com.szampchat.server.message.attachment.entity.MessageAttachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageAttachmentRepository extends CrudRepository<MessageAttachment, Long> {
}
