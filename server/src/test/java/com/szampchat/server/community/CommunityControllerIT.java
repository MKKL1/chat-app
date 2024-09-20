package com.szampchat.server.community;

import com.szampchat.server.PostgresTestContainer;
import com.szampchat.server.RabbitMQTestContainer;
import com.szampchat.server.tools.WithMockCustomUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class CommunityControllerIT implements PostgresTestContainer, RabbitMQTestContainer {

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void beforeAll() {
        PostgresTestContainer.populate();
        postgres.start();
        rabbitmq.start();


    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
        rabbitmq.stop();
    }

    @Test
    @Override
    public void connectionEstablished() {
        PostgresTestContainer.super.connectionEstablished();
        RabbitMQTestContainer.super.connectionEstablished();
    }

    @Test
    void givenAnonymousUser_whenGetCommunity_thenUnauthorized() {
        webTestClient.get().uri("/api/communities/1").exchange()
                .expectStatus().isUnauthorized();
    }

    @WithMockCustomUser
    @Test
    void givenAuthenticatedUserAndNotDefinedCommunity_whenGetCommunity_thenNotFound() {
        webTestClient.get().uri("/api/communities/1").exchange()
                .expectStatus().isNotFound();
    }
}
