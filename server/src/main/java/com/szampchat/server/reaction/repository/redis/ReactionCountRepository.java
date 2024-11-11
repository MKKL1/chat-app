package com.szampchat.server.reaction.repository.redis;

import com.szampchat.server.reaction.entity.Reaction;
import com.szampchat.server.reaction.entity.ReactionCount;
import com.szampchat.server.reaction.entity.ReactionList;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;

@AllArgsConstructor
@Repository
public class ReactionCountRepository {
    private final ReactiveRedisTemplate<String, String> redisReactionTemplate;
    private final ReactiveHashOperations<String, String, Integer> reactiveHashOperations;
    private static final String REACTIONS_CACHE_NAME = "rcnt";
    private final Duration defaultTtl = Duration.ofMinutes(30);

    public Mono<Boolean> save(Long channelId, Long messageId, Collection<ReactionCount> reactionCounts) {
        String key = buildKey(channelId, messageId);
        return Flux.fromIterable(reactionCounts)
                .collectMap(ReactionCount::getEmoji, ReactionCount::getCount)
                .filter(map -> !map.isEmpty())
                .flatMap(map -> reactiveHashOperations.putAll(key, map))
                .flatMap(_ -> redisReactionTemplate.expire(key, defaultTtl))
                .hasElement();
    }

    public Flux<ReactionCount> get(Long channelId, Long messageId) {
        return reactiveHashOperations.entries(buildKey(channelId, messageId))
                .map(entry -> new ReactionCount(entry.getKey(), entry.getValue()));
    }

    public Mono<Boolean> exists(Long channelId, Long messageId) {
        return redisReactionTemplate.hasKey(buildKey(channelId, messageId));
    }

    public Mono<Boolean> expire(Long channelId, Long messageId, Duration duration) {
        return redisReactionTemplate.expire(buildKey(channelId, messageId), duration);
    }

    public Mono<Long> invalidate(Long channelId, Long messageId) {
        return redisReactionTemplate.delete(buildKey(channelId, messageId));
    }

    private String buildKey(Long channelId, Long messageId) {
        return REACTIONS_CACHE_NAME + ":" + channelId + ":" + messageId;
    }

    public Mono<Boolean> increment(Reaction reaction) {
        String key = buildKey(reaction.getChannel(), reaction.getMessage());
        return exists(reaction.getChannel(), reaction.getMessage())
                .flatMap(exists -> {
                    if(exists)
                        return reactiveHashOperations.increment(key, reaction.getEmoji(), 1)
                                .switchIfEmpty(reactiveHashOperations.put(key, reaction.getEmoji(), 1).thenReturn(0L))
                                .thenReturn(true);
                    return Mono.just(false);
                });
    }

    public Mono<Boolean> decrement(Reaction reaction) {
        String key = buildKey(reaction.getChannel(), reaction.getMessage());
        return reactiveHashOperations.increment(key, reaction.getEmoji(), -1)
                .flatMap(newVal -> {
                    if (newVal < 1)
                        return reactiveHashOperations.remove(key, reaction.getEmoji()).thenReturn(true);
                    return Mono.just(false);
                });
    }
}
