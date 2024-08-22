package com.szampchat.server.user.repository;

import com.szampchat.server.user.entity.UserSubject;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserSubjectRepository extends ReactiveCrudRepository<UserSubject, Long> {
    Mono<UserSubject> findBySub(UUID sub);
}
