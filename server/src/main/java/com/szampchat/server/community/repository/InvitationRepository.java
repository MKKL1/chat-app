package com.szampchat.server.community.repository;

import com.szampchat.server.community.entity.Invitation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface InvitationRepository extends R2dbcRepository<Invitation, Long> {

    @Query("SELECT EXISTS (SELECT 1 FROM invitations WHERE id = :invitation AND community_id = :community)")
    Mono<Boolean> isValid(@Param("invitation") Long invitation, @Param("community") Long community);

}
