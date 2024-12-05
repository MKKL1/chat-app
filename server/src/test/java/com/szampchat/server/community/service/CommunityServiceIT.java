package com.szampchat.server.community.service;

import com.szampchat.server.PostgresTestContainer;
import com.szampchat.server.TestSecurityConfiguration;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.tools.populate.CommunityData;
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
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext
@ExtendWith(MockitoExtension.class)
@Import(TestSecurityConfiguration.class)
@ActiveProfiles(value = "test")
public class CommunityServiceIT implements PostgresTestContainer {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CommunityMapper communityMapper;

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

    @Test
    public void givenCommunity_whenGetById_shouldReturnCommunityDTO() {
        CommunityData communityData = testDataGenerator.saveComplexCommunity();
        Long communityId = communityData.getCommunity().getId();

        StepVerifier.create(communityService.getById(communityData.getCommunity().getId()))
                .expectNextMatches(communityDTO -> Objects.equals(communityDTO.getId(), communityId))
                .verifyComplete();
    }

    @Test
    public void givenMultipleCommunitiesAndUser_whenGetUserCommunities_shouldReturnOnlyUserCommunities() {
        int communityCount = 3;

        User user = testDataGenerator.saveUser();
        List<CommunityData> communityDataList = new ArrayList<>();
        for (int i = 0; i < communityCount; i++) {
            List<User> userList = new ArrayList<>();
            userList.add(user);
            communityDataList.add(testDataGenerator.saveComplexCommunity(GenericCommunityGenData.builder()
                            .members(userList)
                    .build()));
        }
        List<CommunityDTO> communityDTOS = communityDataList
                .stream()
                .map(CommunityData::getCommunity)
                .map(community -> communityMapper.toDTO(community)).toList();


        //Save community without tested user
        testDataGenerator.saveComplexCommunity(GenericCommunityGenData.builder().build());

        List<CommunityDTO> communityDTOResult = communityService.getUserCommunities(user.getId()).collectList().block();

        assertThat(communityDTOResult)
                .isNotEmpty()
                .hasSize(communityCount)
                .allMatch(communityDTO -> communityDTOS.stream().anyMatch(x -> Objects.equals(x.getId(), communityDTO.getId())));
    }
}
