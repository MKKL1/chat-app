package com.szampchat.server.reaction.service;

import com.szampchat.server.channel.ChannelService;
import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.reaction.dto.ReactionOverviewBulkDTO;
import com.szampchat.server.reaction.dto.ReactionOverviewDTO;
import com.szampchat.server.reaction.dto.ReactionUpdateDTO;
import com.szampchat.server.reaction.dto.ReactionUsersDTO;
import com.szampchat.server.reaction.dto.request.ReactionUpdateRequest;
import com.szampchat.server.reaction.entity.Reaction;
import com.szampchat.server.reaction.event.ReactionCreateEvent;
import com.szampchat.server.reaction.event.ReactionDeleteEvent;
import com.szampchat.server.reaction.exception.ReactionAlreadyExistsException;
import com.szampchat.server.reaction.exception.ReactionNotFoundException;
import com.szampchat.server.reaction.repository.ReactionRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
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
    private final EventSink eventSink;
    private final ChannelService channelService;

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

    public Mono<Void> createReaction(Long channelId, Long messageId, Long userId, ReactionUpdateRequest request) {
        Reaction reaction = Reaction.builder()
                .emoji(request.getEmoji())
                .channel(channelId)
                .message(messageId)
                .user(userId)
                .build();
        return reactionRepository.save(reaction)
                .onErrorMap(DuplicateKeyException.class, _ -> new ReactionAlreadyExistsException(reaction.getEmoji()))
                .flatMap(savedReaction -> reactionCacheService.addUserReaction(reaction)
                        .then(channelService.getChannelDTO(channelId)
                                .doOnNext(channelDTO ->
                                        eventSink.publish(ReactionCreateEvent.builder()
                                                .recipient(Recipient.builder()
                                                        .context(Recipient.Context.COMMUNITY)
                                                        .id(channelDTO.getCommunityId())
                                                        .build())
                                                .data(ReactionUpdateDTO.builder()
                                                        .channelId(channelId)
                                                        .messageId(messageId)
                                                        .userId(userId)
                                                        .emoji(reaction.getEmoji())
                                                        .build())
                                                .build())
                                )
                        )
                )//Could be published to different scheduler
                .then();
    }

    public Mono<Void> deleteReaction(Long channelId, Long messageId, Long userId, ReactionUpdateRequest request) {
        Reaction reaction = Reaction.builder()
                .emoji(request.getEmoji())
                .channel(channelId)
                .message(messageId)
                .user(userId)
                .build();
        return reactionRepository.deleteByMessageAndChannelAndEmojiAndUser(messageId, channelId, request.getEmoji(), userId)
                .filter(count -> count != 0)
                .flatMap(_ -> channelService.getChannelDTO(channelId)
                        .doOnNext(channelDTO ->
                                eventSink.publish(ReactionDeleteEvent.builder()
                                        .recipient(Recipient.builder()
                                                .context(Recipient.Context.COMMUNITY)
                                                .id(channelDTO.getCommunityId())
                                                .build())
                                        .data(ReactionUpdateDTO.builder()
                                                .channelId(channelId)
                                                .messageId(messageId)
                                                .userId(userId)
                                                .emoji(reaction.getEmoji())
                                                .build())
                                        .build())
                        )
                        .then(reactionCacheService.removeUserReaction(reaction))//Could be published to different scheduler
                )
                .then();
    }
}

