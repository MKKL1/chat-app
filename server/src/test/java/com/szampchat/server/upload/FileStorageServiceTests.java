package com.szampchat.server.upload;

import com.szampchat.server.snowflake.Snowflake;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// don't work 😔
//@ExtendWith(MockitoExtension.class)
//public class FileStorageServiceTests {
//
//    @Mock
//    private Snowflake snowflake;
//
//    @Mock
//    private FilePart mockFilePart;
//
//    @InjectMocks
//    @Spy
//    private FileStorageService fileStorageService;
//
//    private static final String TEST_DIRECTORY = "uploads";
//
//    @BeforeEach
//    public void setup(){
//        mockFilePart = mock(FilePart.class);
//        snowflake = mock(Snowflake.class);
//
//        when(mockFilePart.filename()).thenReturn("testfile.txt");
//        when(mockFilePart.transferTo(any(Path.class))).thenAnswer(invocation -> {
//            Path destination = invocation.getArgument(0);
//            Files.createDirectories(destination.getParent());
//            return Mono.empty();
//        });
//        when(snowflake.nextId()).thenReturn(1714867200000L);
//    }
//
//    @Test
//    public void getFileFromPath(){
//    }
//
//    @Test
//    public void saveFileToDisk(){
//        FilePath filePath = FilePath.MESSAGE;
//
//        Mono<String> path = fileStorageService.save(mockFilePart, filePath);
//
//        StepVerifier.create(path)
//            .expectNextMatches(uploadedPath -> {
//                Path expectedPath = Paths.get(TEST_DIRECTORY, "communities", "1714867200000.png");
//                return uploadedPath.equals(expectedPath.toString());
//            })
//            .verifyComplete();
//
//        verify(mockFilePart, times(1)).transferTo(any(Path.class));
//    }
//
//    @Test
//    public void deleteFileFromDisk(){
//
//    }
//
//    @Test
//    public void deleteAllFilesFromDisk(){
//
//    }
//
//}
