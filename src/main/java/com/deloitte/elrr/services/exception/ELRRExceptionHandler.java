package com.deloitte.elrr.services.exception;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author mnelakurti
 *
 */
@ControllerAdvice
@Slf4j
public class ELRRExceptionHandler extends ResponseEntityExceptionHandler {
    /**
     *
     * @param ex
     * @param request
     * @return ResponseEntity<?>
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> recordNotFoundException(
            final ResourceNotFoundException ex, final WebRequest request) {
        ELRRErrorDetails errorDetails = new ELRRErrorDetails(new Date(),
                ex.getMessage(), request.getDescription(false), null);
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    /**
     * @param ex
     * @param headers
     * @param status
     * @param request
     * @return ResponseEntity
     */
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException ex, final HttpHeaders headers,
            final HttpStatus status, final WebRequest request) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        ELRRErrorDetails errorDetails = new ELRRErrorDetails(new Date(),
                "Validation Failed", request.getDescription(false), details);
        return new ResponseEntity<>(errorDetails, status);
    }
    /**
     *
     * @param ex
     * @param request
     * @return ResponseEntity<Object>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> elrrExcpetionHandler(final Exception ex,
            final WebRequest request) {
        ELRRErrorDetails errorDetails = new ELRRErrorDetails(new Date(),
                ex.getMessage(), request.getDescription(false), null);
                log.error("General Error", ex);
        return new ResponseEntity<>(errorDetails,
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
