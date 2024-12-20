package com.szampchat.server.reaction.repository.redis;

import com.szampchat.server.reaction.dto.ReactionOverviewDTO;
import com.szampchat.server.reaction.dto.ReactionUsersDTO;
import com.szampchat.server.reaction.entity.EmojiUserPair;
import com.szampchat.server.reaction.entity.ReactionCount;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class ReactionUsersRepository {
    private final ReactiveRedisTemplate<String, String> redisStringTemplate;
    private final ReactiveSetOperations<String, String> setOps;
    private static final String REACTIONS_USERS_CACHE_NAME = "rusr";
    private final Duration defaultTtl = Duration.ofMinutes(30);

    public ReactionUsersRepository(ReactiveRedisTemplate<String, String> redisStringTemplate) {
        this.redisStringTemplate = redisStringTemplate;
        setOps = redisStringTemplate.opsForSet();
    }

    public Mono<Boolean> save(Long channelId, Long messageId, Collection<EmojiUserPair> emojiUserPairs) {
        String key = buildKey(channelId, messageId);
        return Flux.fromIterable(emojiUserPairs)
                .map(this::combineUserAndEmoji)
                .collectList()
                .filter(list -> !list.isEmpty()) //Should I remove old object if it exists?
                .flatMap(reactionValueList -> setOps.add(key, reactionValueList.toArray(new String[0])))
                .flatMap(_ -> redisStringTemplate.expire(key, defaultTtl));
    }

    public Mono<Map<EmojiUserPair, Boolean>> contains(Long channelId, Long messageId, Collection<EmojiUserPair> emojiUserPairs) {
        Map<EmojiUserPair, String> userEmojis = new HashMap<>(); //Map of (emoji,user) -> "emoji:user"
        for (EmojiUserPair emojiUserPair : emojiUserPairs) {
            userEmojis.put(emojiUserPair, combineUserAndEmoji(emojiUserPair));
        }

        return setOps.isMember(buildKey(channelId, messageId), userEmojis.values().toArray()) //Returns Map of "emoji:user"->isMember
                .flatMap(isMemberMap -> Flux.fromIterable(emojiUserPairs)
                                .collectMap(emojiUserPair -> emojiUserPair, emojiUserPair -> {
                                    String emojiUserKey = userEmojis.get(emojiUserPair);
                                    return isMemberMap.get(emojiUserKey);
                                })
                );
    }

    public Mono<Boolean> add(Long channelId, Long messageId, EmojiUserPair emojiUserPair) {
        String key = buildKey(channelId, messageId);
        return exists(channelId, messageId)
                .flatMap(exists -> {
                    if(exists)
                        return setOps.add(key, combineUserAndEmoji(emojiUserPair))
                                .thenReturn(true);
                    return Mono.just(false);
                });
    }

    public Mono<Long> remove(Long channelId, Long messageId, EmojiUserPair emojiUserPair) {
        return setOps.remove(buildKey(channelId, messageId), combineUserAndEmoji(emojiUserPair));
    }

    public Mono<Boolean> exists(Long channelId, Long messageId) {
        return redisStringTemplate.hasKey(buildKey(channelId, messageId));
    }

    public Mono<Boolean> expire(Long channelId, Long messageId, Duration duration) {
        return redisStringTemplate.expire(buildKey(channelId, messageId), duration);
    }

    public Mono<Long> invalidate(Long channelId, Long messageId) {
        return redisStringTemplate.delete(buildKey(channelId, messageId));
    }

    private String combineUserAndEmoji(EmojiUserPair emojiUserPair) {
        return emojiUserPair.getEmoji() + ":" + emojiUserPair.getUserId();
    }

    private String buildKey(Long channelId, Long messageId) {
        return REACTIONS_USERS_CACHE_NAME + ":" + channelId + ":" + messageId;
    }
}
