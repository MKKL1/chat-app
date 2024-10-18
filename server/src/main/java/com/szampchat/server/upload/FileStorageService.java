package com.szampchat.server.upload;

import com.szampchat.server.snowflake.Snowflake;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
@Slf4j
public class FileStorageService {
    private static Snowflake snowflake;

    FileStorageService(Snowflake snowflake){
        this.snowflake = snowflake;
    }

    public void init(){
        try{
            Files.createDirectories(Paths.get("uploads/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> getFile(String filename){
        try {
            String currentDirectory = System.getProperty("user.dir");
            Path filePath = Paths.get(currentDirectory, "uploads", filename);
            File file = filePath.toFile();

            // checking if file exists
            if (file.exists() && file.isFile()) {
                // getting file
                FileSystemResource resource = new FileSystemResource(file);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

                // returning file
                return new ResponseEntity<>(resource, headers, HttpStatus.OK);
            } else {
                // Status if file doesn't exist
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
                snowflake.nextId() + filename.substring(dotIndex).toLowerCase());
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
                .then(Mono.just(uploadPath.subpath(1, uploadPath.getNameCount()).toString())))
            .onErrorMap(e -> new RuntimeException("File saving failed: " + e.getMessage()));
    }

    public void delete(String filePath) throws FileSystemException {
        File file = new File("uploads", filePath);

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

}
