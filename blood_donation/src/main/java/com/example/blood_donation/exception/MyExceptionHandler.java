package com.example.blood_donation.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;

public class MyExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleBadRequestException(MethodArgumentNotValidException exception){
        String responseMessage ="";
        for(FieldError fileError: exception.getFieldErrors()){
            responseMessage += fileError.getDefaultMessage()+ "\n";
        }
        return new ResponseEntity(responseMessage, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler (DataIntegrityViolationException.class)
    public ResponseEntity handleBadRequestException(DataIntegrityViolationException exception){
        return new ResponseEntity("Duplicate", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler (AuthenticationException.class)
    public ResponseEntity handleAuthenticationException(AuthenticationException exception){
        return new ResponseEntity(exception.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
