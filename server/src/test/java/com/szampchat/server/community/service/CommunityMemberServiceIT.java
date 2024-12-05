package com.szampchat.server.community.service;

import com.szampchat.server.PostgresTestContainer;
import com.szampchat.server.TestSecurityConfiguration;
import com.szampchat.server.tools.populate.GenericCommunityGenData;
import com.szampchat.server.tools.populate.TestDataGenerator;
import com.szampchat.server.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfiguration.class)
@ActiveProfiles(value = "test")
public class CommunityMemberServiceIT implements PostgresTestContainer{

    @Autowired
    private TestDataGenerator testDataGenerator;


    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @Test
    @Override
    public void connectionEstablished() {
        PostgresTestContainer.super.connectionEstablished();
    }


    public void givenCommunityAndUser_whenIsMember_shouldReturnTrue() {
        User user = testDataGenerator.saveUser();

        testDataGenerator.saveComplexCommunity(GenericCommunityGenData.builder()
                        .members(List.of(user))
                .build());



    }
}
