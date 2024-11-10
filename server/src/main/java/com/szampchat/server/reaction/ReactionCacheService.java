package com.szampchat.server.reaction;

import com.szampchat.server.reaction.dto.ReactionOverviewDTO;
import com.szampchat.server.reaction.dto.ReactionUsersBulkDTO;
import com.szampchat.server.reaction.dto.ReactionUsersDTO;
import com.szampchat.server.reaction.dto.ReactionListDTO;
import com.szampchat.server.reaction.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReactionCacheService {
    private final ReactionRepository reactionRepository;
    private final ReactiveRedisTemplate<String, String> redisStringTemplate;
    private final ReactiveRedisTemplate<String, Long> redisLongTemplate;


    public ReactionCacheService(ReactionRepository reactionRepository,
                                @Qualifier("stringReactiveRedisTemplate") ReactiveRedisTemplate<String, String> redisStringTemplate,
                                @Qualifier("longReactiveRedisTemplate") ReactiveRedisTemplate<String, Long> redisLongTemplate) {
        this.reactionRepository = reactionRepository;
        this.redisStringTemplate = redisStringTemplate;
        this.redisLongTemplate = redisLongTemplate;
    }

    private static final String REACTIONS_CACHE_NAME = "emojis:";
    private static final String REACTIONS_USERS_CACHE_NAME = "reactions:";

//    public Flux<ReactionUsersDTO> fetchReactionEmojis(Long channelId, Long messageId) {
//
//
////        return reactionRepository.fetchGroupedReactions(channelId, messageId)
////                .collectList()
////                .flatMap(reactionUsersDTOS -> cacheReactions(channelId, messageId, reactionUsersDTOS)
////                        .then(Mono.just(reactionUsersDTOS))
////                )
////                .flatMapMany(Flux::fromIterable);
//    }

    private Flux<Long> cacheReactions(Long channelId, Long messageId, List<ReactionUsersDTO> reactionUsersDTOS) {
        final String cacheKeyEmojiSet = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        final String cacheKeyUserSet = REACTIONS_USERS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        ReactiveSetOperations<String, String> setOpsString = redisStringTemplate.opsForSet();
        ReactiveSetOperations<String, Long> setOpsLong = redisLongTemplate.opsForSet();

        return setOpsString.add(cacheKeyEmojiSet, reactionUsersDTOS.stream()
                        .map(ReactionUsersDTO::getEmoji)
                        .toList()
                        .toArray(new String[0])
                )
                .thenMany(Flux.fromIterable(reactionUsersDTOS))
                .flatMap(dto -> setOpsLong.add(cacheKeyUserSet + ":" + dto.getEmoji(), dto.getUsers().toArray(new Long[0])));
    }

    public Flux<ReactionOverviewDTO> fetchAndCacheReactions(Long channelId, Long messageId, Long userId) {
        final String cacheKeyEmojiSet = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        return redisStringTemplate.hasKey(cacheKeyEmojiSet)
                .flatMapMany(hasKey -> {
                    if(hasKey) {
                        return getFromCache(channelId, messageId, userId);
                    } else {
                        return getFromDB(channelId, messageId, userId);
                    }
                });
    }

    private Flux<ReactionOverviewDTO> getFromCache(Long channelId, Long messageId, Long userId) {
        final String cacheKeyEmojiSet = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        final String cacheKeyUserSet = REACTIONS_USERS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;

        ReactiveSetOperations<String, String> setOpsString = redisStringTemplate.opsForSet();
        ReactiveSetOperations<String, Long> setOpsLong = redisLongTemplate.opsForSet();

        return setOpsString.members(cacheKeyEmojiSet)
                .flatMap(emoji -> {
                    String emojiKey = cacheKeyUserSet + ":" + emoji;
                    Mono<Long> countMono = setOpsLong.size(emojiKey);
                    Mono<Boolean> meMono = setOpsLong.isMember(emojiKey, userId);

                    return Mono.zip(countMono, meMono)
                            .map(tuple -> new ReactionOverviewDTO(emoji, tuple.getT1().intValue(), tuple.getT2()));
                });
    }

    private Flux<ReactionOverviewDTO> getFromDB(Long channelId, Long messageId, Long userId) {
        return reactionRepository.fetchGroupedReactions(channelId, messageId)
                .collectList()
                .flatMap(reactionUsersDTOS -> cacheReactions(channelId, messageId, reactionUsersDTOS)
                        .then(Mono.just(reactionUsersDTOS))
                )
                .flatMapMany(Flux::fromIterable)
                .map(dto -> {
                    int count = dto.getUsers().size();
                    boolean me = dto.getUsers().contains(userId);
                    return new ReactionOverviewDTO(dto.getEmoji(), count, me);
                });
    }

//    public Mono<Map<Long, Collection<ReactionUsersDTO>>> fetchAndCacheReactionsBulk(Long channelId, Collection<Long> messageIds) {
//        List<String> cacheKeys = messageIds.stream()
//                .map(messageId -> REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId)
//                .toList();
//
//        //TODO add comments
//        return redisTemplate.opsForValue().multiGet(cacheKeys)
//                .flatMap(cachedEntries -> {
//                    Map<Long, Collection<ReactionUsersDTO>> cachedResults = new HashMap<>();
//                    Iterator<Long> messageIdIterator = messageIds.iterator();
//
//                    List<Long> missingIds = new ArrayList<>();
//                    for (ReactionListDTO dto : cachedEntries) {
//                        Long messageId = messageIdIterator.next();
//
//                        if (dto != null && dto.getReactions() != null)
//                            cachedResults.put(messageId, dto.getReactions());
//
//                        else missingIds.add(messageId);
//
//                    }
//
//                    if (missingIds.isEmpty()) return Mono.just(cachedResults);
//
//                    return reactionRepository.fetchGroupedReactionsBulk(channelId, missingIds)
//                            .collectMultimap(ReactionUsersBulkDTO::getMessageId,
//                                    reactionUsersBulkDTO -> new ReactionUsersDTO(
//                                            reactionUsersBulkDTO.getEmoji(),
//                                            reactionUsersBulkDTO.getUsers()
//                                    )
//                            )
//                            .flatMap(map -> Flux.fromIterable(map.entrySet())
//                                            .map(entry ->
//                                                    Map.entry(
//                                                            REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + entry.getKey(),
//                                                            new ReactionListDTO(entry.getValue().stream().toList())
//                                                    )
//                                            )
//                                            .collectMap(Map.Entry::getKey, Map.Entry::getValue)
//                                            .flatMap(missingReactions -> redisTemplate.opsForValue().multiSet(missingReactions))
//                                            .thenReturn(map)
//                            ).map(map -> Stream.concat(
//                                            cachedResults.entrySet().stream(),
//                                            map.entrySet().stream()
//                                    ).collect(Collectors.<Map.Entry<Long, Collection<ReactionUsersDTO>>, Long, Collection<ReactionUsersDTO>>toMap(
//                                            Map.Entry::getKey,
//                                            Map.Entry::getValue,
//                                            (existing, replacement) -> replacement
//                                            ))
//                            );
//                });
//    }
}


