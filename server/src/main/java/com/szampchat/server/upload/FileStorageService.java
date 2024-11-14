package com.szampchat.server.upload;

import com.szampchat.server.upload.dto.FileDTO;
import com.szampchat.server.upload.dto.FileDownloadDTO;
import com.szampchat.server.upload.entity.FileEntity;
import com.szampchat.server.upload.exception.FileNotFoundException;
import com.szampchat.server.upload.exception.FileOperationException;
import com.szampchat.server.upload.repository.FileRepository;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
@Service
public class FileStorageService {
//    private final SnowflakeGen snowflakeGen;
    private final String currentDirectory = System.getProperty("user.dir");
    private final FileRepository fileRepository;

    @PostConstruct
    public void init(){
        try{
            Files.createDirectories(Paths.get("uploads/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Mono<FileDownloadDTO> downloadFile(UUID fileId) {
        return getFileDTO(fileId)
                .flatMap(fileDTO -> {
                    File file = Paths.get(currentDirectory, "uploads", fileDTO.getPath()).toFile();
                    if(!file.exists() || !file.isFile()) {
                        return Mono.error(new FileNotFoundException(fileId));
                    }
                    FileSystemResource resource = new FileSystemResource(file);
                    return Mono.just(new FileDownloadDTO(fileDTO, resource));
                });
    }

    public Mono<FileDTO> getFileDTO(UUID fileID) {
        return fileRepository.findById(fileID)
                .switchIfEmpty(Mono.error(new FileNotFoundException(fileID)))
                .map(this::toDTO);
    }

    public Mono<FileDTO> upload(FilePart file, FilePathType path) {
        UUID uuid = UUID.randomUUID();
        return Mono.just(buildFilePath(path, uuid.toString()))
        .subscribeOn(Schedulers.boundedElastic())
        .doOnNext(uploadPath -> {
            try {
                Files.createDirectories(uploadPath.getParent());
            } catch (IOException e) {
                throw new FileOperationException("Could not create upload directory: " + e.getMessage());
            }
        })
        .flatMap(uploadPath -> file.transferTo(uploadPath)
            .then(fileRepository.save(FileEntity.builder()
                            .id(uuid)
                            .path(uploadPath.subpath(1, uploadPath.getNameCount()).toString())
                            .build())
            )
        )
        .map(this::toDTO)
        .onErrorMap(e -> new FileOperationException("Failed to save file: " + e.getMessage()));
    }

    public Mono<Void> delete(UUID fileID) {
        return getFileDTO(fileID)
                .flatMap(fileDTO -> {
                    File file = buildFilePath(fileDTO).toFile();
                    if(!file.exists()){
                        return Mono.error(new FileOperationException("File doesn't exist"));
                    }

                    if(!file.delete()){
                        return Mono.error(new FileOperationException("Cannot delete file"));
                    }
                    return Mono.empty();
                });
    }

    private Path buildFilePath(FilePathType path, String filename) {
        String filePath = switch(path) {
            case COMMUNITY -> "communities";
            case AVATAR -> "avatars";
            case MESSAGE -> "messages";
        };

        return Paths.get("uploads", filePath, filename);
    }

    private Path buildFilePath(FileEntity fileEntity) {
        return Paths.get("uploads", fileEntity.getPath());
    }

    private Path buildFilePath(FileDTO fileDTO) {
        return Paths.get("uploads", fileDTO.getPath());
    }

    private FileDTO toDTO(FileEntity entity) {
        return FileDTO.builder()
                .id(entity.getId())
                .path(entity.getPath())
                .build();
    }
}
