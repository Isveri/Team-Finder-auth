package com.evi.teamfinderauth.exception;

import lombok.Getter;

@Getter
public class TokenAlreadySendException extends RuntimeException{

    private final String code = "15";

    public TokenAlreadySendException(String message){super(message);}
}
