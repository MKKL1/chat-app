package com.szampchat.server.upload;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@RestController
@AllArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/file/{fileId}")
    public Mono<ResponseEntity<FileSystemResource>> getFile(@PathVariable String fileId) {
    log.info("File request of {}", fileId);
    return fileStorageService.downloadFile(fileId)
        .map(fileDownloadDTO -> {
            String filename = Path.of(fileDownloadDTO.getFileDTO().getPath()).getFileName().toString();
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
            headers.add(HttpHeaders.CONTENT_TYPE, fileDownloadDTO.getFileDTO().getMime());

            return new ResponseEntity<>(fileDownloadDTO.getResource(), headers, HttpStatus.OK);
        })
        .onErrorResume(Mono::error);
    }

}
