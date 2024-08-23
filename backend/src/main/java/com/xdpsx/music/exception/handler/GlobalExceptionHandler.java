package com.xdpsx.music.exception.handler;

import com.xdpsx.music.dto.common.ErrorDetails;
import com.xdpsx.music.util.I18nUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final I18nUtils i18nUtils;

    @ExceptionHandler(LockedException.class)
    @ResponseStatus(HttpStatus.LOCKED)
    public ErrorDetails handleLocked(HttpServletRequest request, LockedException e){
        log.error(e.getMessage(), e);

        ErrorDetails errorDetails = new ErrorDetails(e.getMessage());
        errorDetails.setStatus(HttpStatus.LOCKED.value());
        errorDetails.setPath(request.getServletPath());

        return errorDetails;
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDetails handleDisabled(HttpServletRequest request, DisabledException e){
        log.error(e.getMessage(), e);

        ErrorDetails errorDetails = new ErrorDetails(e.getMessage());
        errorDetails.setStatus(HttpStatus.FORBIDDEN.value());
        errorDetails.setPath(request.getServletPath());

        return errorDetails;
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDetails handleBadCredentials(HttpServletRequest request, BadCredentialsException e){
        log.error(e.getMessage(), e);

        ErrorDetails errorDetails = new ErrorDetails(i18nUtils.getWrongCredMsg());
        errorDetails.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorDetails.setPath(request.getServletPath());

        return errorDetails;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleMethodArgumentNotValid(HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails("Validation Error");
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetails.setPath(request.getServletPath());

        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        errorDetails.setDetails(errors.stream()
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage)));
        return errorDetails;
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleMissingServletRequestPart(HttpServletRequest request, MissingServletRequestPartException e) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails("Parameter " + e.getRequestPartName()  + " is missing in the request");
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDetails handleMaxUploadSizeExceeded(HttpServletRequest request, MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails(i18nUtils.getMaxUploadSizeMsg());
        errorDetails.setStatus(HttpStatus.BAD_REQUEST.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDetails handleException(HttpServletRequest request, Exception e) {
        log.error(e.getMessage(), e);
        ErrorDetails errorDetails = new ErrorDetails("Internal Server Error");
        errorDetails.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorDetails.setPath(request.getServletPath());
        return errorDetails;
    }
}
