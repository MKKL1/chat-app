package com.szampchat.server.community.service;

import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.community.event.MemberCreateEvent;
import com.szampchat.server.community.repository.CommunityMemberRepository;
import com.szampchat.server.event.EventSink;
import com.szampchat.server.event.data.Recipient;
import com.szampchat.server.shared.CustomPrincipalProvider;
import com.szampchat.server.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service("communityMemberService")
@Slf4j
public class CommunityMemberService {
    private final CommunityMemberRepository communityMemberRepository;
    private final CustomPrincipalProvider customPrincipalProvider;
    private final UserService userService;
    private final EventSink eventSink;

    public Mono<Boolean> isMember(Long communityId, Long userId) {
        return communityMemberRepository.isMemberOfCommunity(communityId, userId);
    }

    public Mono<Boolean> isMember(Long communityId) {
        return customPrincipalProvider.getPrincipal()
                .flatMap(user -> isMember(communityId, user.getUserId()))
                .onErrorReturn(false);
    }

    public Mono<Boolean> isNotMember(Long communityId, Long userId) {
        return isMember(communityId, userId).map(val -> !val);
    }

    public Mono<Boolean> isNotMember(Long communityId) {
        return customPrincipalProvider.getPrincipal()
                .flatMap(user -> isNotMember(communityId, user.getUserId()))
                .onErrorReturn(false);
    }

    public Flux<CommunityMember> getByCommunityId(Long communityId) {
        return communityMemberRepository.findByCommunityId(communityId);
    }

    public Mono<CommunityMember> create(Long communityId, Long userId) {
        return communityMemberRepository.save(new CommunityMember(communityId, userId))
                .flatMap(communityMember -> userService.getUser(communityMember.getUserId())
                        .doOnNext(userDTO -> eventSink.publish(MemberCreateEvent.builder()
                                .recipient(Recipient.builder()
                                        .context(Recipient.Context.COMMUNITY)
                                        .id(communityMember.getCommunityId())
                                        .build()
                                )
                                .data(userDTO)
                                .build())
                        )
                        .then(Mono.just(communityMember))
                );
    }
}
