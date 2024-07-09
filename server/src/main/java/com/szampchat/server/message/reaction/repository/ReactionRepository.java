package com.szampchat.server.message.reaction.repository;

import com.szampchat.server.message.reaction.ReactionPreview;
import com.szampchat.server.message.reaction.entity.Reaction;
import com.szampchat.server.message.reaction.entity.ReactionId;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ReactionRepository extends R2dbcRepository<Reaction, ReactionId> {
    @Query("""
    SELECT emoji,
           count(emoji),
           BOOL_OR(user_id = :userId) as me
    FROM reactions
    WHERE channel_id = :channelId and message_id = :messageId
    GROUP BY emoji""")
    Flux<ReactionPreview> fetchGroupedReactions(@Param("channelId") Long channelId,
                                                @Param("messageId") Long messageId,
                                                @Param("userId") Long userId);
}
