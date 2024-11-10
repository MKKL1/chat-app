package com.szampchat.server.reaction.service;

import com.szampchat.server.reaction.dto.*;
import com.szampchat.server.reaction.entity.ReactionCount;
import com.szampchat.server.reaction.entity.ReactionList;
import com.szampchat.server.reaction.repository.ReactionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReactionCacheService {
    private final ReactionRepository reactionRepository;
    private final ReactiveRedisTemplate<String, String> redisStringTemplate;
    private final ReactiveRedisTemplate<String, ReactionList> redisReactionTemplate;

    public ReactionCacheService(ReactionRepository reactionRepository,
                                @Qualifier("stringReactiveRedisTemplate") ReactiveRedisTemplate<String, String> redisStringTemplate,
                                @Qualifier("reactionListDTOReactiveRedisTemplate") ReactiveRedisTemplate<String, ReactionList> redisReactionTemplate) {
        this.reactionRepository = reactionRepository;
        this.redisStringTemplate = redisStringTemplate;
        this.redisReactionTemplate = redisReactionTemplate;
    }

    private static final String REACTIONS_CACHE_NAME = "emojis:";
    private static final String REACTIONS_USERS_CACHE_NAME = "reactions:";

    private Mono<Void> cacheReactions(Long channelId, Long messageId, Collection<ReactionUsersDTO> reactionUsersDTOS) {
        final String cacheKeyReaction = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        final String cacheKeyUserSet = REACTIONS_USERS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;

        ReactiveSetOperations<String, String> setOpsString = redisStringTemplate.opsForSet();
        ReactiveValueOperations<String, ReactionList> valueOpsReaction = redisReactionTemplate.opsForValue();

//        Mono<Boolean> saveReactionDto = Flux.fromIterable(reactionUsersDTOS)
//                .map(dto -> new ReactionCount(dto.getEmoji(), dto.getUsers().size()))
//                .collectList()
//                .filter(list -> !list.isEmpty())
//                .flatMap(list -> valueOpsReaction.set(cacheKeyReaction, new ReactionList(list), Duration.ofMinutes(5)));

//        Mono<Long> saveUsers = Flux.fromIterable(reactionUsersDTOS)
//                .flatMap(dto -> {
//                    String emoji = dto.getEmoji();
//                    return Flux.fromStream(dto.getUsers().stream().map(user -> combineUserAndEmoji(emoji, user)));
//                })
//                .collectList()
//                .filter(list -> !list.isEmpty())
//                .flatMap(reactionValueList -> setOpsString.add(cacheKeyUserSet, reactionValueList.toArray(new String[0])));

        return Mono.zip(saveReactionDto, saveUsers).then();
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
        final String cacheKeyReaction = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        ReactiveValueOperations<String, ReactionList> valueOpsReaction = redisReactionTemplate.opsForValue();

        return valueOpsReaction.get(cacheKeyReaction)
                .flatMapMany(list -> processReactionList(channelId, messageId, userId, list));
    }

    private Flux<ReactionOverviewDTO> processReactionList(Long channelId, Long messageId, Long userId, ReactionList reactionList) {
        final String cacheKeyUserSet = REACTIONS_USERS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        ReactiveSetOperations<String, String> setOpsString = redisStringTemplate.opsForSet();

        return Mono.just(reactionList)
                .map(ReactionList::getReactions)
                .filter(reactionCounts -> !reactionCounts.isEmpty())
                .flatMapMany(reactionCounts -> {
                    Map<String, String> userEmojis = new HashMap<>(); //Map of emoji-> "emoji:user"
                    for (ReactionCount reactionCount : reactionCounts) {
                        userEmojis.put(reactionCount.getEmoji(), combineUserAndEmoji(reactionCount.getEmoji(), userId));
                    }

                    return setOpsString.isMember(cacheKeyUserSet, userEmojis.values().toArray()) //Returns Map of "emoji:user"->me
                            .flatMapMany(isMeMap -> Flux.fromIterable(reactionCounts)
                                    .map(reactionCount -> {
                                        String emoji = reactionCount.getEmoji();
                                        String emojiUserKey = userEmojis.get(emoji);
                                        boolean isMe = isMeMap.get(emojiUserKey);
                                        return new ReactionOverviewDTO(emoji, reactionCount.getCount(), isMe);
                                    })
                            );
                });
    }



    private Flux<ReactionOverviewDTO> getFromDB(Long channelId, Long messageId, Long userId) {
        return reactionRepository.fetchGroupedReactions(channelId, messageId)
                .collectList()
                //TODO Could be zipped
                .flatMap(reactionUsersDTOS -> cacheReactions(channelId, messageId, reactionUsersDTOS)
                        .then(Mono.just(reactionUsersDTOS))
                )
                .flatMapMany(list -> reactionUsersToOverview(list, userId));
    }

    private Flux<ReactionOverviewDTO> reactionUsersToOverview(Collection<ReactionUsersDTO> reactionUsersDTOS, Long userId) {
        return Flux.fromIterable(reactionUsersDTOS)
                .map(dto -> {
                    int count = dto.getUsers().size();
                    boolean me = dto.getUsers().contains(userId);
                    return new ReactionOverviewDTO(dto.getEmoji(), count, me);
                });
    }

    public Flux<ReactionOverviewBulkDTO> fetchAndCacheReactionsBulk(Long channelId, Collection<Long> messageIds, Long userId) {
        List<String> cacheKeys = messageIds.stream()
                .map(messageId -> REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId)
                .toList();

        ReactiveValueOperations<String, ReactionList> valueOpsReaction = redisReactionTemplate.opsForValue();

        return valueOpsReaction.multiGet(cacheKeys)
                .flatMapMany(cachedResults -> {
                    // Map cache results to messageIds
                    Map<Long, ReactionList> cachedMap = new HashMap<>();
                    Iterator<Long> messageIdIterator = messageIds.iterator();

                    for (ReactionList cachedResult : cachedResults) {
                        Long messageId = messageIdIterator.next();
                        if (cachedResult != null) {
                            cachedMap.put(messageId, cachedResult);
                        }
                    }

                    // Filter out messageIds that need fetching from DB
                    List<Long> missingMessageIds = messageIds.stream()
                            .filter(messageId -> !cachedMap.containsKey(messageId))
                            .toList();

                    // Fetch missing data from DB, cache it, and process it
                    Flux<ReactionOverviewBulkDTO> dbResults = reactionRepository.fetchGroupedReactionsBulk(channelId, missingMessageIds)
                            .collectList()
                            .flatMapMany(bulkReactions -> {
                                Map<Long, List<ReactionUsersDTO>> reactionsByMessage = bulkReactions.stream()
                                        .collect(Collectors.groupingBy(
                                                ReactionUsersBulkDTO::getMessageId,
                                                Collectors.mapping(
                                                        dto -> new ReactionUsersDTO(dto.getEmoji(), dto.getUsers()),
                                                        Collectors.toList()
                                                )
                                        ));

                                return Flux.fromIterable(reactionsByMessage.entrySet())
                                        .flatMap(entry -> {
                                            Long messageId = entry.getKey();
                                            List<ReactionUsersDTO> reactionUsersDTOS = entry.getValue();

                                            return cacheReactions(channelId, messageId, reactionUsersDTOS)
                                                    .then(reactionUsersToOverview(reactionUsersDTOS, userId)
                                                            .collectList()
                                                            .map(list -> new ReactionOverviewBulkDTO(messageId, list))
                                                    );
                                        });
                            });

                    Flux<ReactionOverviewBulkDTO> cachedResultsFlux = Flux.fromIterable(cachedMap.entrySet())
                            .flatMap(entry -> processReactionList(channelId, entry.getKey(), userId, entry.getValue())
                                    .collectList()
                                    .map(list -> new ReactionOverviewBulkDTO(entry.getKey(), list)));

                    // Combine cached and fetched data
                    return Flux.concat(cachedResultsFlux, dbResults);
                });
    }

//    public Mono<Boolean> hasReaction(Long channelId, Long messageId, Long userId, String emoji) {
//
//    }

    public Mono<Boolean> save(Long channelId, Long messageId, Long userId, String emoji) {
        //Retrieve old reaction list
        final String cacheKeyReaction = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        ReactiveValueOperations<String, ReactionList> valueOpsString = redisReactionTemplate.opsForValue();

        final String cacheKeyUserSet = REACTIONS_USERS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        ReactiveSetOperations<String, String> setOpsString = redisStringTemplate.opsForSet();

        String userEmojiValue = combineUserAndEmoji(emoji, userId);
        return redisStringTemplate.hasKey(cacheKeyReaction)
                .filter(hasKey -> hasKey)
                .flatMap(_ -> setOpsString.isMember(cacheKeyUserSet, userEmojiValue))
                .filter(isMember -> !isMember)
                .flatMap(_ -> setOpsString.add(cacheKeyUserSet, userEmojiValue))
                .flatMap(_ -> valueOpsString.get(cacheKeyReaction))
                //If it's empty, that's ok
                .flatMap(oldValue -> {
                    //Find reaction
                    Optional<ReactionCount> reactionCountOpt = oldValue.getReactions()
                            .stream().filter(reactionCount -> reactionCount.getEmoji().equals(emoji))
                            .findFirst();

                    if(reactionCountOpt.isEmpty()) {
                        oldValue.getReactions().add(new ReactionCount(emoji, 1));
                    } else {
                        ReactionCount reactionCount = reactionCountOpt.get();
                        reactionCount.setCount(reactionCount.getCount() + 1);
                    }

                    return valueOpsString.set(cacheKeyReaction, oldValue, Duration.ofMinutes(5));
                });
    }

    public Mono<Boolean> remove(Long channelId, Long messageId, Long userId, String emoji) {
        final String cacheKeyReaction = REACTIONS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        ReactiveValueOperations<String, ReactionList> valueOpsString = redisReactionTemplate.opsForValue();

        final String cacheKeyUserSet = REACTIONS_USERS_CACHE_NAME + "channel:" + channelId + ":message:" + messageId;
        ReactiveSetOperations<String, String> setOpsString = redisStringTemplate.opsForSet();

        String userEmojiValue = combineUserAndEmoji(emoji, userId);
        return redisStringTemplate.hasKey(cacheKeyReaction)
                .filter(hasKey -> hasKey)
                .flatMap(_ -> setOpsString.remove(cacheKeyUserSet, userEmojiValue))
                .filter(index -> index != 0)
                .flatMap(_ -> valueOpsString.get(cacheKeyReaction))
                .flatMap(oldValue -> {
                    Optional<ReactionCount> reactionCountOpt = oldValue.getReactions()
                            .stream().filter(reactionCount -> reactionCount.getEmoji().equals(emoji))
                            .findFirst();

                    if(reactionCountOpt.isPresent()) {
                        ReactionCount reactionCount = reactionCountOpt.get();
                        if(reactionCount.getCount() < 2) {
                            oldValue.getReactions().remove(reactionCount);
                        } else {
                            reactionCount.setCount(reactionCount.getCount() - 1);
                        }

                    }

                    return valueOpsString.set(cacheKeyReaction, oldValue, Duration.ofMinutes(5));
                });
    }
}


