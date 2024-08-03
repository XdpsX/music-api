package com.xdpsx.music.exception.handler;

import com.xdpsx.music.dto.common.ErrorDetails;
import com.xdpsx.music.exception.BadRequestException;
import com.xdpsx.music.exception.DuplicateResourceException;
import com.xdpsx.music.exception.ResourceNotFoundException;
import com.xdpsx.music.exception.TooManyRequestsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(TooManyRequestsException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ErrorDetails handleTooManyRequests(HttpServletRequest request, TooManyRequestsException e){
        log.error(e.getMessage(), e);

        ErrorDetails errorDetails = new ErrorDetails(e.getMessage());
        errorDetails.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }

    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDetails handleDuplicateResource(HttpServletRequest request, DuplicateResourceException e){
        log.error(e.getMessage(), e);

        ErrorDetails errorDetails = new ErrorDetails(e.getMessage());
        errorDetails.setStatus(HttpStatus.CONFLICT.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDetails handleResourceNotFound(HttpServletRequest request, ResourceNotFoundException e) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage());
        errorDetails.setStatus(HttpStatus.NOT_FOUND.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleBadRequest(HttpServletRequest request, BadRequestException e){
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(e.getMessage());
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }
}
