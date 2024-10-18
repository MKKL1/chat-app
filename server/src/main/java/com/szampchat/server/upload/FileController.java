package com.szampchat.server.upload;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

@RestController
@AllArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    // I use ServerWebExchange because @PathVariable caused issues
    // with mapping routes - basically if path contains slashes /
    // it would try to get static resource
    @GetMapping("/file/**")
    public ResponseEntity<?> getFile(ServerWebExchange exchange){
        String filepath = exchange.getRequest().getURI().getPath().replace("/api/file/", "");
        return fileStorageService.getFile(filepath);
    }

}
