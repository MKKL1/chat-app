package com.szampchat.server.message.reaction.repository;

import com.szampchat.server.message.reaction.ReactionId;
import com.szampchat.server.message.reaction.entity.Reaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends CrudRepository<Reaction, ReactionId> {
}
