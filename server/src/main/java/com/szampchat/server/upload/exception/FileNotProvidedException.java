package com.szampchat.server.upload.exception;

import com.szampchat.server.shared.exception.BadRequestException;

public class FileNotProvidedException extends BadRequestException {
    public FileNotProvidedException() {
        super("No file provided in request");
    }
}
