package com.szampchat.server.upload;

import lombok.AllArgsConstructor;
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

@RestController
@AllArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/file/{fileId}")
    public Mono<ResponseEntity<?>> getFile(@PathVariable String fileId) {
        return fileStorageService.downloadFile(UUID.fromString(fileId))
                .map(fileDownloadDTO -> {
                    HttpHeaders headers = new HttpHeaders();
                    String filename = Path.of(fileDownloadDTO.getFileDTO().getPath()).getFileName().toString();
                    headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

                    return new ResponseEntity<>(fileDownloadDTO.getResource(), headers, HttpStatus.OK);
                });
    }

}
