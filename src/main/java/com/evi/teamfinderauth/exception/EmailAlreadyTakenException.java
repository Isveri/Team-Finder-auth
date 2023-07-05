package com.evi.teamfinderauth.exception;

import lombok.Getter;

@Getter
public class EmailAlreadyTakenException extends RuntimeException{
    private final String code = "31";

    public EmailAlreadyTakenException(String message){super(message);}

}
