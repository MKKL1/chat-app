package com.szampchat.server.reaction.repository;

import com.szampchat.server.reaction.dto.ReactionDTO;
import com.szampchat.server.reaction.dto.ReactionOverviewDTO;
import com.szampchat.server.reaction.entity.Reaction;
import com.szampchat.server.reaction.entity.ReactionId;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReactionRepository extends R2dbcRepository<Reaction, ReactionId> {
    @Query("""
        SELECT r.emoji, array_agg(r.user_id) AS users
        FROM reactions r
        WHERE r.channel_id = :channelId AND r.message_id = :messageId
        GROUP BY r.emoji
""")
    Flux<ReactionDTO> fetchGroupedReactions(@Param("channelId") Long channelId,
                                            @Param("messageId") Long messageId);

}
