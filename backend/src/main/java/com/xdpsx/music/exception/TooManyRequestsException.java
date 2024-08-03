package com.xdpsx.music.exception;

public class TooManyRequestsException extends RuntimeException{

    public TooManyRequestsException(String message) {
        super(message);
    }
}
