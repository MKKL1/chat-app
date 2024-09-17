package com.szampchat.server.event.rabbitmq;

import com.szampchat.server.event.data.EventType;
import com.szampchat.server.event.data.Recipient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
public class RouteMapperTests {
    private RouteMapper routeMapper;

    @BeforeEach
    void setUp() {
        routeMapper = new RouteMapper();
    }

    private final static Recipient recipientCommunity = Recipient.builder()
            .context(Recipient.Context.COMMUNITY)
            .id(1)
            .build();

    private final static Recipient recipientUser = Recipient.builder()
            .context(Recipient.Context.USER)
            .id(2)
            .build();

    private static Stream<Arguments> provideRouteKeysFor_toRouteKey_shouldReturnCorrectRouteKey() {
        return Stream.<Arguments>builder()
                .add(Arguments.of(EventType.MESSAGES, recipientCommunity, "messages.community.1"))
                .add(Arguments.of(EventType.MESSAGES, recipientUser, "messages.user.2"))
                .build();
    }

    @ParameterizedTest
    @MethodSource("provideRouteKeysFor_toRouteKey_shouldReturnCorrectRouteKey")
    public void toRouteKey_shouldReturnCorrectRouteKey(EventType eventType, Recipient recipient, String routeKey) {
        Assertions.assertEquals(routeKey, routeMapper.toRouteKey(eventType, recipient));
    }

    public void toRouteKey_shouldThrowExceptionForUnknownEventType() {
        //I have no idea how to test it
    }


}
