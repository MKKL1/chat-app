package com.szampchat.server.message.repository;

import com.szampchat.server.message.entity.MessageAttachment;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MessageAttachmentRepository extends R2dbcRepository<MessageAttachment, Long> {
    //TODO
//    Flux<MessageAttachment> fetchBulkMessageAttachmentsByMessage();
    Mono<MessageAttachment> findMessageAttachmentByMessageAndChannel(Long message, Long channel);
}
