package com.szampchat.server.upload;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping("/file/{filepath}")
    public ResponseEntity<Resource> getFile(@PathVariable String filepath){
        return fileStorageService.getFile(filepath);
    }

}
