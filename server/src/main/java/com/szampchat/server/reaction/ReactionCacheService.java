package com.szampchat.server.reaction;

import com.szampchat.server.reaction.dto.ReactionDTO;
import com.szampchat.server.reaction.repository.ReactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@AllArgsConstructor
@Service
public class ReactionCacheService {
    private final ReactionRepository reactionRepository;
    private final ReactiveRedisTemplate<String, ReactionDTO> redisTemplate;

    private static final String REACTIONS_CACHE_NAME = "reactions:";

    public Mono<List<ReactionDTO>> fetchAndCacheReactions(Long channelId, Long messageId) {
        String cacheKey = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;

        // Try to retrieve the data from Redis cache
        return redisTemplate.opsForList().range(cacheKey, 0, -1)
                .collectList()
                .flatMap(cachedReactions -> {
                    if (!cachedReactions.isEmpty()) {
                        return Mono.just(cachedReactions);
                    }
                    // If not found in cache, fetch from repository and cache the result
                    return reactionRepository.fetchGroupedReactions(channelId, messageId)
                            .collectList()
                            .flatMap(reactions ->
                                    // Save each item in the list to Redis with the cache key and an expiration time
                                    redisTemplate.opsForList().rightPushAll(cacheKey, reactions)
                                            .then(redisTemplate.expire(cacheKey, Duration.ofHours(1)))
                                            .thenReturn(reactions)
                            );
                });
    }
}
