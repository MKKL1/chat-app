package com.szampchat.server.socket;

import com.rabbitmq.client.Delivery;
import com.szampchat.server.community.exception.NotCommunityMemberException;
import com.szampchat.server.community.service.CommunityMemberService;
import com.szampchat.server.socket.auth.RSocketPrincipalProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.rabbitmq.BindingSpecification;
import reactor.rabbitmq.QueueSpecification;
import reactor.rabbitmq.Receiver;
import reactor.rabbitmq.Sender;

import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class EventService {

    private final RSocketPrincipalProvider principalProvider;
    private final CommunityMemberService communityMemberService;
    private final Receiver receiver;
    private final Sender sender;

    public Flux<Delivery> receiveCommunityEvents(final long communityId, final String exchange, final String routingKey) {
        //I had to get principal manually, as providing it in method parameter caused error
        return principalProvider.getPrincipal()
                //In the same way I had to get principal manually, I have to check permission manually
                .flatMap(currentUser -> communityMemberService.isMember(communityId, currentUser.getUserId())
                        .flatMap(isMember -> {
                            if (!isMember) return Mono.error(new NotCommunityMemberException());
                            return Mono.just(currentUser);
                        }))
                .flatMapMany(currentUser -> {
                    final String queue = "user." + currentUser.getUserId() + "-" + UUID.randomUUID();
                    //Declaring a unique temporary queue for this stream request
                    //TODO there is no check for failure, in which case receiver would fail, as there is no queue or binding
                    return sender.declare(QueueSpecification.queue(queue).autoDelete(true))
                            .then(sender.bind(BindingSpecification.queueBinding(exchange, routingKey, queue)))
                            //Receiving messages to flux
                            .thenMany(receiver.consumeAutoAck(queue));
                });
    }
}
