package com.evi.teamfinderauth.exception.handler;


import com.evi.teamfinderauth.exception.EmailAlreadyTakenException;
import com.evi.teamfinderauth.exception.UserNotFoundException;
import com.evi.teamfinderauth.exception.WrongPasswordException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorCodeMsg> userNotFound(UserNotFoundException e){
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeMsg.builder().code(e.getCode()).build());
    }

    @ExceptionHandler({EmailAlreadyTakenException.class})
    public ResponseEntity<ErrorCodeMsg> alreadyBannedException(EmailAlreadyTakenException e){
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeMsg.builder().code(e.getCode()).build());
    }

    @ExceptionHandler({WrongPasswordException.class})
    public ResponseEntity<ErrorCodeMsg> alreadyInvitedException(WrongPasswordException e){
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(ErrorCodeMsg.builder().code(e.getCode()).build());
    }


}
