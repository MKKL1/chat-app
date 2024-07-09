package com.szampchat.server.message.attachment.repository;

import com.szampchat.server.message.attachment.entity.MessageAttachment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MessageAttachmentRepository extends R2dbcRepository<MessageAttachment, Long> {
    //TODO
//    Flux<MessageAttachment> fetchBulkMessageAttachmentsByMessage();
}
