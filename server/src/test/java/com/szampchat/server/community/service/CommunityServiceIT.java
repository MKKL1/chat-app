package com.szampchat.server.community.service;

import com.szampchat.server.PostgresTestContainer;
import com.szampchat.server.TestSecurityConfiguration;
import com.szampchat.server.community.dto.CommunityDTO;
import com.szampchat.server.community.dto.request.CommunityCreateRequest;
import com.szampchat.server.community.entity.CommunityMember;
import com.szampchat.server.tools.populate.CommunityData;
import com.szampchat.server.tools.populate.GenericCommunityGenData;
import com.szampchat.server.tools.populate.TestDataGenerator;
import com.szampchat.server.upload.FilePathType;
import com.szampchat.server.upload.FileStorageService;
import com.szampchat.server.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
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

    @MockBean
    private FileStorageService fileStorageService;

    @MockBean
    private CommunityMemberService communityMemberService;


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

    @Test
    public void givenCommunityCreateRequest_whenSave_shouldReturnSavedCommunity() {
        User owner = testDataGenerator.saveUser();
        Long ownerId = owner.getId();

        String communityName = "Community name";

        when(fileStorageService.upload(any(FilePart.class), eq(FilePathType.COMMUNITY)))
                .thenReturn(Mono.empty());//Shouldn't be called

        when(communityMemberService.create(anyLong(), eq(ownerId))).thenAnswer(invocationOnMock ->
                Mono.just(CommunityMember.builder()
                .userId(ownerId)
                .communityId((Long) invocationOnMock.getArguments()[0])
                .build()));

        StepVerifier.create(communityService.save(new CommunityCreateRequest(communityName), null, ownerId))
                .expectNextMatches(community -> community.getOwnerId().equals(ownerId)
                        && community.getName().equals(communityName))
                .verifyComplete();
    }

//    @Test
//    public void givenCommunityEditRequest_whenEditCommunity_shouldReturnEditedCommunity() {
//        when(fileStorageService.replace(any(FilePart.class), eq(FilePathType.COMMUNITY), any(UUID.class)))
//                .thenReturn(Mono.empty());//Shouldn't be called
//
//
//    }
}
