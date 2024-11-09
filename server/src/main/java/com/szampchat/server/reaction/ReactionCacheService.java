package com.szampchat.server.reaction;

import com.szampchat.server.reaction.dto.ReactionUsersBulkDTO;
import com.szampchat.server.reaction.dto.ReactionUsersDTO;
import com.szampchat.server.reaction.dto.ReactionListDTO;
import com.szampchat.server.reaction.repository.ReactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class ReactionCacheService {
    private final ReactionRepository reactionRepository;
    private final ReactiveRedisTemplate<String, ReactionListDTO> redisTemplate;

    private static final String REACTIONS_CACHE_NAME = "reactions:";

    public Mono<Collection<ReactionUsersDTO>> fetchAndCacheReactions(Long channelId, Long messageId) {
        String cacheKey = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;

        return redisTemplate.opsForValue().get(cacheKey)
                .map(ReactionListDTO::getReactions)
                .flatMap(cachedReactions -> {
                    if (cachedReactions != null && !cachedReactions.isEmpty()) {
                        return Mono.just(cachedReactions);
                    }
                    return Mono.empty();
                })
                .switchIfEmpty(reactionRepository.fetchGroupedReactions(channelId, messageId)
                        .collectList()
                        .flatMap(reactions ->
                                redisTemplate.opsForValue().set(cacheKey, new ReactionListDTO(reactions), Duration.ofHours(1))
                                        .thenReturn(reactions)
                        ));
    }

    public Mono<Map<Long, Collection<ReactionUsersDTO>>> fetchAndCacheReactionsBulk(Long channelId, Collection<Long> messageIds) {
        List<String> cacheKeys = messageIds.stream()
                .map(messageId -> REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId)
                .toList();

        return redisTemplate.opsForValue().multiGet(cacheKeys)
                .flatMap(cachedEntries -> {
                    Map<Long, Collection<ReactionUsersDTO>> cachedResults = new HashMap<>();
                    Iterator<Long> messageIdIterator = messageIds.iterator();

                    List<Long> missingIds = new ArrayList<>();
                    for (ReactionListDTO dto : cachedEntries) {
                        Long messageId = messageIdIterator.next();

                        if (dto != null && dto.getReactions() != null)
                            cachedResults.put(messageId, dto.getReactions());

                        else missingIds.add(messageId);

                    }

                    if (missingIds.isEmpty()) return Mono.just(cachedResults);

                    return reactionRepository.fetchGroupedReactionsBulk(channelId, missingIds)
                            .collectMultimap(ReactionUsersBulkDTO::getMessageId,
                                    reactionUsersBulkDTO -> new ReactionUsersDTO(
                                            reactionUsersBulkDTO.getEmoji(),
                                            reactionUsersBulkDTO.getUsers()
                                    )
                            )
                            .flatMap(map -> Flux.fromIterable(map.entrySet())
                                            .map(entry ->
                                                    Map.entry(
                                                            REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + entry.getKey(),
                                                            new ReactionListDTO(entry.getValue().stream().toList())
                                                    )
                                            )
                                            .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                                            .flatMap(missingReactions -> redisTemplate.opsForValue().multiSet(missingReactions))
                                            .thenReturn(map)
                            ).map(map -> Stream.concat(
                                            cachedResults.entrySet().stream(),
                                            map.entrySet().stream()
                                    ).collect(Collectors.<Map.Entry<Long, Collection<ReactionUsersDTO>>, Long, Collection<ReactionUsersDTO>>toMap(
                                            Map.Entry::getKey,
                                            Map.Entry::getValue,
                                            (existing, replacement) -> replacement
                                            ))
                            );
                });
    }
}


