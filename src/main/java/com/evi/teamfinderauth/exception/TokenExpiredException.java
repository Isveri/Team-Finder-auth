package com.evi.teamfinderauth.exception;

import lombok.Getter;

@Getter
public class TokenExpiredException extends RuntimeException {

    private final String code = "16";

    public TokenExpiredException(String message) {
        super(message);
    }
}
