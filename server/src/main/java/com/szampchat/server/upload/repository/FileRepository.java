package com.szampchat.server.upload.repository;

import com.szampchat.server.upload.entity.FileEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface FileRepository extends R2dbcRepository<FileEntity, UUID> {
}
