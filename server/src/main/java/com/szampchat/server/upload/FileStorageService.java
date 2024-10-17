package com.szampchat.server.upload;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Random;

@Service
public class FileStorageService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int length = 64;

    public void init(){
        try{
            Files.createDirectories(Paths.get("uploads"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<Resource> getFile(String path){
        try {
            Path filePath = Paths.get(path);
            File file = filePath.toFile();

            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Resource resource = new FileSystemResource(file);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Mono<String> save(FilePart file, FilePath path){
        return Mono.fromCallable(() -> {
            String filePath = switch(path) {
                case COMMUNITY -> "communities";
                case AVATAR -> "avatars";
                case MESSAGE -> "messages";
            };

            String filename = file.filename();
            int dotIndex = filename.lastIndexOf(".");

            return Paths.get(
                "uploads",
                filePath,
                generateRandomName() + filename.substring(dotIndex).toLowerCase());
            })
            .doOnNext(uploadPath -> {
                try {
                    Files.createDirectories(uploadPath.getParent());
                } catch (IOException e) {
                    throw new RuntimeException("Could not create upload directory: " + e.getMessage());
                }
            })
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(uploadPath -> file.transferTo(uploadPath)
                .then(Mono.just(uploadPath.toString())))
            .onErrorMap(e -> new RuntimeException("File saving failed: " + e.getMessage()));
    }

    public void delete(String filePath) throws FileSystemException {
        File file = new File(filePath);

        if(!file.exists()){
            throw new FileSystemException("File doesn't exist");
        }

        if(!file.delete()){
            throw new FileSystemException("Cannot delete file");
        }

    }

    public void deleteAll(){
        FileSystemUtils.deleteRecursively(Paths.get("uploads").toFile());
    }

    private String generateRandomName(){
        var random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

}
