package com.szampchat.server.upload.exception;

import com.szampchat.server.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class FileOperationException extends RuntimeException {
    public FileOperationException(String s) {
        super(s);
    }
}
