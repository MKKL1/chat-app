package com.szampchat.server.event;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.InternalEvent;
import com.szampchat.server.event.data.Recipient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class EventSinkTests {
    private EventSink eventSink;

    @BeforeEach
    void setUp() {
        eventSink = new EventSink();
    }


    @Test
    void shouldEmitEventWhenPublished() {
        InternalEvent<String> event = getExampleEvent("event1");

        StepVerifier.create(eventSink.asFlux())
                .then(() -> eventSink.publish(event))
                .expectNext(event)
                .thenCancel()
                .verify();
    }

    @Test
    void shouldHandleBackpressure() {
        InternalEvent<String> event1 = getExampleEvent("event1");
        InternalEvent<String> event2 = getExampleEvent("event2");

        StepVerifier.create(eventSink.asFlux().publishOn(Schedulers.immediate()))
                .then(() -> {
                    eventSink.publish(event1);
                    eventSink.publish(event2);
                })
                .expectNext(event1)
                .expectNext(event2)
                .thenCancel()
                .verify();
    }


    private static InternalEvent<String> getExampleEvent(String eventData) {
        return new InternalEvent<String>() {
            @Override
            public String getName() {
                return "name";
            }

            @Override
            public EventType getType() {
                return EventType.MESSAGES;
            }

            @Override
            public Recipient getRecipient() {
                return Recipient.builder()
                        .id(123)
                        .context(Recipient.Context.COMMUNITY)
                        .build();
            }

            @Override
            public String getData() {
                return eventData;
            }
        };
    }
}
