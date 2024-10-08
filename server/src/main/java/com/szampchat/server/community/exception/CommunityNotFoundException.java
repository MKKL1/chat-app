package com.szampchat.server.community.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Community not found")
public class CommunityNotFoundException extends RuntimeException{
}
