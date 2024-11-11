package com.szampchat.server.reaction.service;

import com.szampchat.server.reaction.dto.ReactionOverviewBulkDTO;
import com.szampchat.server.reaction.dto.ReactionOverviewDTO;
import com.szampchat.server.reaction.dto.ReactionUsersDTO;
import com.szampchat.server.reaction.dto.request.ReactionUpdateRequest;
import com.szampchat.server.reaction.entity.Reaction;
import com.szampchat.server.reaction.exception.ReactionNotFoundException;
import com.szampchat.server.reaction.repository.ReactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collection;

@AllArgsConstructor
@Service
public class ReactionService {
    private final ReactionCacheService reactionCacheService;
    private final ReactionRepository reactionRepository;

    public Flux<ReactionOverviewDTO> getReactionsForUser(Long channelId, Long messageId, Long userId) {
        return reactionCacheService.get(channelId, messageId, userId)
                .switchIfEmpty(reactionRepository.fetchGroupedReactions(channelId, messageId)
                            .collectList()
                            .flatMap(reactionUsersDTOS -> reactionCacheService.save(channelId, messageId, reactionUsersDTOS)
                                    .publishOn(Schedulers.boundedElastic())
                                    .thenReturn(reactionUsersDTOS))
                            .flatMapMany(reactionUsersDTOS -> Flux.fromIterable(reactionUsersDTOS)
                                    .map(reactionUsersDTO -> toOverview(reactionUsersDTO, userId))
                            )
                );
    }

    private ReactionOverviewDTO toOverview(ReactionUsersDTO reactionUsersDTO, Long userId) {
        return ReactionOverviewDTO.builder()
                .emoji(reactionUsersDTO.getEmoji())
                .count(reactionUsersDTO.getUsers().size())
                .me(reactionUsersDTO.getUsers().contains(userId))
                .build();
    }

//    public Flux<ReactionOverviewBulkDTO> getReactionsForUserBulk(Long channelId, Collection<Long> messageIds, Long userId) {
//        return reactionCacheService.fetchAndCacheReactionsBulk(channelId, messageIds, userId);
//    }

//    public Mono<Void> createReaction(Long channelId, Long messageId, Long userId, ReactionUpdateRequest request) {
//        return reactionRepository.save(Reaction.builder()
//                .emoji(request.getEmoji())
//                .channel(channelId)
//                .message(messageId)
//                .user(userId)
//                .build()
//        ).flatMap(_ -> reactionCacheService.save(channelId, messageId, userId, request.getEmoji())).then();
//        //TODO send event
//    }
//    public Mono<Void> deleteReaction(Long channelId, Long messageId, Long userId, ReactionUpdateRequest request) {
//        return reactionRepository.deleteByMessageAndChannelAndEmojiAndUser(messageId, channelId, request.getEmoji(), userId)
//                .switchIfEmpty(Mono.error(new ReactionNotFoundException(request.getEmoji())))
//                .flatMap(_ -> reactionCacheService.remove(channelId, messageId, userId, request.getEmoji())).then();
//        //TODO send event
//    }
}

