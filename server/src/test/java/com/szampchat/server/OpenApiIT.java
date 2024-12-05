package com.szampchat.server;

import com.szampchat.server.community.entity.Community;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureWebTestClient(timeout = "3600000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//Had to add dirties context, as rsocket port is not randomized
//TODO find a way to reuse context
@DirtiesContext
@Import(TestSecurityConfiguration.class)
@ActiveProfiles(value = "test")
public class OpenApiIT {
    @Autowired
    protected WebTestClient client;

    //Generating openapi file here as I cannot get it to work with maven
    @Test
    void generateOpenApiFile() {
        String outputPath = "../docs/static/openapi.yaml";

        byte[] bytes = client.get().uri("/v3/api-docs.yaml").exchange()
                .expectStatus().isOk()
                .expectBody()
                .returnResult()
                .getResponseBody();

        File outputFile = new File(outputPath);

        assert bytes != null;

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {

            fos.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
