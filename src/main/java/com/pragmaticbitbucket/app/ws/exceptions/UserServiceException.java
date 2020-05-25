package com.pragmaticbitbucket.app.ws.exceptions;

public class UserServiceException extends RuntimeException{
    private static long serialVersionUID = 1L;

    public UserServiceException(String message) {
        super(message);
    }
}
