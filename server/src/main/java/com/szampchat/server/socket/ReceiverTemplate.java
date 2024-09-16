package com.szampchat.server.socket;

import com.rabbitmq.client.Delivery;
import com.szampchat.server.community.exception.NotCommunityMemberException;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.community.service.CommunityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.authorization.AuthorizationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.BindingSpecification;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Component
public class ReceiverTemplate {

    private final RSocketPrincipalProvider principalProvider;
    private final CommunityMemberService communityMemberService;
    private final Receiver receiver;
    private final Sender sender;

    public Flux<Delivery> receiveCommunityEvents(final long communityId, final String exchange, final String routingKey) {
        //I had to get principal manually, as providing it in method parameter caused error
        return principalProvider.getPrincipal()
                .flatMap(currentUser -> communityMemberService.isMember(communityId, currentUser.getUserId())
                        .flatMap(isMember -> {
                            if (!isMember) return Mono.error(new NotCommunityMemberException());
                            return Mono.just(currentUser);
                        }))
                .flatMapMany(currentUser -> {
                    final String queue = "user." + currentUser.getUserId() + "-" + UUID.randomUUID();
                    return sender.declare(QueueSpecification.queue(queue).autoDelete(true))
                            .then(sender.bind(BindingSpecification.queueBinding(exchange, routingKey, queue)))
                            .thenMany(receiver.consumeAutoAck(queue));
                });
    }
}
