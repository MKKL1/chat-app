package com.szampchat.server.message.reaction.repository;

import com.szampchat.server.message.reaction.entity.Reaction;
import com.szampchat.server.message.reaction.entity.ReactionId;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends R2dbcRepository<Reaction, ReactionId> {
}
