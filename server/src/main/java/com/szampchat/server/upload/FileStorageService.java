package com.szampchat.server.upload;

import com.szampchat.server.upload.dto.FileDTO;
import com.szampchat.server.upload.dto.FileDownloadDTO;
import com.szampchat.server.upload.entity.FileEntity;
import com.szampchat.server.upload.exception.FileNotFoundException;
import com.szampchat.server.upload.exception.FileOperationException;
import com.szampchat.server.upload.repository.FileRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
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
            Files.createDirectories(Paths.get("uploads/communities/"));
            Files.createDirectories(Paths.get("uploads/avatars/"));
            Files.createDirectories(Paths.get("uploads/messages/"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Mono<FileDTO> getFileDTO(@Nonnull UUID fileID) {
        return fileRepository.findById(fileID)
                .switchIfEmpty(Mono.error(new FileNotFoundException(fileID)))
                .map(this::toDTO);
    }

    public Mono<FileDownloadDTO> downloadFile(String fileIdString) {
        return Mono.justOrEmpty(fileIdString)
                .switchIfEmpty(Mono.error(new FileNotFoundException(fileIdString)))
                .flatMap(fileId -> downloadFile(UUID.fromString(fileId)))
                .onErrorMap(IllegalArgumentException.class, e -> new FileNotFoundException(fileIdString));
    }

    public Mono<FileDownloadDTO> downloadFile(@Nonnull UUID fileId) {
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

    public Mono<FileDTO> upload(@Nonnull FilePart file, @Nonnull FilePathType path) {
        UUID uuid = UUID.randomUUID();
        String extension = getFileExtension(new File(file.filename()));
        Path uploadPath = buildFilePath(path, uuid + extension);
        Mono<Void> uploadFileMono = file.transferTo(uploadPath).subscribeOn(Schedulers.boundedElastic());

        return fileRepository.save(FileEntity.builder()
                        .path(uploadPath.subpath(1, uploadPath.getNameCount()).toString())
                        .mime("images/" + extension) //TODO temporary solution
                        .build())
                .flatMap(fileEntity -> uploadFileMono.thenReturn(fileEntity))
                .map(this::toDTO)
                .onErrorMap(e -> new FileOperationException("Failed to save file: " + e.getMessage()));
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return name.substring(lastIndexOf);
    }

    public Mono<Void> delete(@Nonnull UUID fileID) {
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

    public Mono<FileDTO> replace(@Nullable FilePart filePart, FilePathType pathType, @Nullable UUID fileId) {
        // If no file is provided, return an empty Mono
        if (filePart == null)
            return Mono.empty();

        // If fileId is provided, delete the existing file first
        Mono<Void> deleteExistingFileMono = (fileId != null) ? delete(fileId) : Mono.empty();

        return deleteExistingFileMono.then(upload(filePart, pathType));
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
                .mime(entity.getMime())
                .build();
    }
}
