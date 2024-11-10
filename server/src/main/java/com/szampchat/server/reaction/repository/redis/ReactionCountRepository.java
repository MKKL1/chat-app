package com.szampchat.server.reaction.repository.redis;

import com.redis.lettucemod.api.StatefulRedisModulesConnection;
import com.redis.lettucemod.api.reactive.RedisModulesReactiveCommands;
import com.szampchat.server.reaction.entity.ReactionCount;
import com.szampchat.server.reaction.entity.ReactionList;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;

@Repository
public class ReactionCountRepository {
    private final ReactiveRedisTemplate<String, ReactionList> redisReactionTemplate;
    private final ReactiveValueOperations<String, ReactionList> valueOpsReaction;
    private final RedisModulesReactiveCommands<String, ReactionList> reactiveModulesCommands;
    private static final String REACTIONS_CACHE_NAME = "rcnt:";
    private final Duration defaultTtl = Duration.ofMinutes(30);

    public ReactionCountRepository(ReactiveRedisTemplate<String, ReactionList> redisReactionTemplate,
                                   StatefulRedisModulesConnection<String, ReactionList> statefulRedisModulesConnection) {
        this.redisReactionTemplate = redisReactionTemplate;
        this.valueOpsReaction = redisReactionTemplate.opsForValue();
        this.reactiveModulesCommands = statefulRedisModulesConnection.reactive();
    }

    public Mono<Boolean> save(Long channelId, Long messageId, Collection<ReactionCount> reactionCounts) {
        return Flux.fromIterable(reactionCounts)
                .map(dto -> new ReactionCount(dto.getEmoji(), dto.getCount()))
                .collectList()
                .filter(list -> !list.isEmpty())
                .flatMap(list -> valueOpsReaction.set(buildKey(channelId, messageId), new ReactionList(list), defaultTtl));
    }

    public Flux<ReactionCount> get(Long channelId, Long messageId) {
        return valueOpsReaction.get(buildKey(channelId, messageId))
                .map(ReactionList::getReactions)
                .flatMapIterable(reactionCounts -> reactionCounts);
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
}
