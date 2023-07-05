package com.evi.teamfinderauth.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException{
    private final String code = "1";
    public UserNotFoundException(String message) {
        super(message);
    }
}
