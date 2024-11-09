package com.szampchat.server.reaction;

import com.szampchat.server.reaction.dto.ReactionDTO;
import com.szampchat.server.reaction.repository.ReactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
@Service
public class ReactionCacheService {
    private final ReactionRepository reactionRepository;

    private static final String REACTIONS_CACHE_NAME = "reactions";

    @Cacheable(value = REACTIONS_CACHE_NAME, key = "'channel:' + #channelId + ':message:' + #messageId")
    public Mono<List<ReactionDTO>> fetchAndCacheReactions(Long channelId, Long messageId) {
        return reactionRepository.fetchGroupedReactions(channelId, messageId)
                .collectList();
    }
}
