package io.davidabejirin.bvnvalidationassessment.exception;

import io.davidabejirin.bvnvalidationassessment.util.ResponseStatusCode;
import io.davidabejirin.bvnvalidationassessment.payload.BVNResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerAdvice {

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BVNResponse constraintViolationException(BindException ex) {
        log.error(ex.getMessage(), ex);
        FieldError fieldError = ex.getFieldErrors().get(0);
        BVNResponse response = BVNResponse.builder()
                .message(fieldError.getDefaultMessage())
                .code(ResponseStatusCode.BAD_REQUEST)
                .build();
        return response;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BVNResponse generalExceptions(Exception ex) {
        String message = "There was an error processing the request, Please try again later...";
        log.error(message, ex);
        BVNResponse response = BVNResponse.builder()
                .message(message)
                .code(ResponseStatusCode.INTERNAL_SERVER_ERROR)
                .build();
        return response;
    }
}
