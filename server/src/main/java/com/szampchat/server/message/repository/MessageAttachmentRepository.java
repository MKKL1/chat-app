package com.szampchat.server.message.repository;

import com.szampchat.server.message.entity.MessageAttachment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageAttachmentRepository extends R2dbcRepository<MessageAttachment, Long> {
    //TODO
//    Flux<MessageAttachment> fetchBulkMessageAttachmentsByMessage();
}
