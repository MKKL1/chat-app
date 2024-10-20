package com.szampchat.server.tools.populate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class QuickTestData {

    @Autowired
    private TestDataGenerator testDataGenerator;

    //Generating it in test, as I can't get generator to work outside of test root nor do I want to waste more time on it
//    @Test
//    public void saveTestData() {
//        CommunityData communityData = testDataGenerator.saveComplexCommunity(
//                GenericCommunityGenData.builder()
//                        .randomRoles(5)
//                        .rolesPerUser(2)
//                        .randomMessages(10)
//                        .randomMembers(10)
//                        .randomChannels(5)
//                        .build());
//    }
}
