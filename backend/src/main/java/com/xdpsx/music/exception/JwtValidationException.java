package com.xdpsx.music.exception;

public class JwtValidationException extends RuntimeException {

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
