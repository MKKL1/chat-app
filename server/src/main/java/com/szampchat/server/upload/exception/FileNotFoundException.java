package com.szampchat.server.upload.exception;

import com.szampchat.server.shared.exception.NotFoundException;

import java.util.UUID;

public class FileNotFoundException extends NotFoundException {
    public FileNotFoundException(UUID fileId) {
        super("file", "File " + fileId);
    }

    public FileNotFoundException(String fileId) {
        super("file", "File " + fileId);
    }
}
