package com.evi.teamfinderauth.exception.handler;


import com.evi.teamfinderauth.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.channels.AcceptPendingException;


@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorCodeMsg> userNotFound(UserNotFoundException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeMsg.builder().code(e.getCode()).build());
    }

    @ExceptionHandler({EmailAlreadyTakenException.class})
    public ResponseEntity<ErrorCodeMsg> emailAlreadyTakenException(EmailAlreadyTakenException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeMsg.builder().code(e.getCode()).build());
    }

    @ExceptionHandler({WrongPasswordException.class})
    public ResponseEntity<ErrorCodeMsg> wrongPasswordException(WrongPasswordException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeMsg.builder().code(e.getCode()).build());
    }

    @ExceptionHandler({AccountBannedException.class})
    public ResponseEntity<ErrorCodeMsg> accountBannedException(AccountBannedException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeMsg.builder().code(e.getCode()).build());
    }

    @ExceptionHandler({AccountNotEnabledException.class})
    public ResponseEntity<ErrorCodeMsg> accountNotEnabledException(AccountNotEnabledException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeMsg.builder().code(e.getCode()).build());
    }

    @ExceptionHandler({TokenAlreadySendException.class})
    public ResponseEntity<ErrorCodeMsg> tokenAlreadySendException(TokenAlreadySendException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeMsg.builder().code(e.getCode()).build());
    }

    @ExceptionHandler({TokenExpiredException.class})
    public ResponseEntity<ErrorCodeMsg> tokenExpiredException(TokenExpiredException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeMsg.builder().code(e.getCode()).build());
    }


}
