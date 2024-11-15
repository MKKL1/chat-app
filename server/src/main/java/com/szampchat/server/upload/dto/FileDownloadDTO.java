package com.szampchat.server.upload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.FileSystemResource;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDownloadDTO {
    private FileDTO fileDTO;
    private FileSystemResource resource;
}
