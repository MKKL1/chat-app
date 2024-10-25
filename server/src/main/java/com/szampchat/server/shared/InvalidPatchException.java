package com.szampchat.server.shared;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class InvalidPatchException extends RuntimeException{
    public InvalidPatchException(String failedToApplyPatch, Exception e) {
        super(failedToApplyPatch, e);
    }
}
