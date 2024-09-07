package com.szampchat.server.socket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebFlux
@Slf4j
public class DefaultWebSocketHandler implements WebSocketHandler {

    @Bean
    public SimpleUrlHandlerMapping simpleUrlHandlerMapping() {
        Map<String, WebSocketHandler> urlMap = new HashMap<>();
        urlMap.put("/ws", this);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setUrlMap(urlMap);
        handlerMapping.setOrder(1);

        return handlerMapping;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(session.getHandshakeInfo().getUri());
        Map<String,String> queryParams = builder.build().getQueryParams().toSingleValueMap();
        String uniqueId = queryParams.get("email");
        log.info(STR."Websocket connected for id \{uniqueId}");
        Flux<WebSocketMessage> messageFlux = session.receive()
                .map(msg -> STR."Server received: \{msg.getPayloadAsText()}")
                .map(session::textMessage);

        return session.send(messageFlux);
    }
}
