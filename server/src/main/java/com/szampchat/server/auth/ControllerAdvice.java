package com.szampchat.server.auth;

import com.szampchat.server.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerAdvice {

    //Custom exception handler, as this exception should be handled by frontend (by redirecting to 'continue registration' page)
    @ExceptionHandler(UserNotRegisteredException.class)
    public ResponseEntity<Void> handleUserNotFoundException(UserNotRegisteredException e) {
        return new ResponseEntity<>(HttpStatusCode.valueOf(419));
    }
}
