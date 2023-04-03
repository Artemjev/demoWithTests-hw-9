package com.example.demowithtests.util.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmployeeIsBooleanFieldValidException.class)
    public ResponseEntity<?> EmployeeIsBooleanFieldValidException(
            EmployeeIsBooleanFieldValidException exception
            , WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                exception.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.EXPECTATION_FAILED);
    }


    @ExceptionHandler(EmployeeUnconfirmedDataException.class)
    //    public ResponseEntity<ErrorDetails> employeeUnconfirmedDataException(WebRequest request) {
    public ResponseEntity<ErrorDetails> employeeUnconfirmedDataException(
            EmployeeUnconfirmedDataException exception,
            WebRequest request) {

        ErrorDetails errorDetails = new ErrorDetails(new Date(),
                exception.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<?> emailSendingException(EmailSendingException exception,
                                                   WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                exception.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler(NoSuchEmployeeException.class)
    public ResponseEntity<?> noSuchEmployeeException(WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                "There is no such employee",
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    //--------------------------------------------
    @ExceptionHandler(ResourceNotVisibleException.class)
    public ResponseEntity<?> resourceNotVisibleException(WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                "Resource is not visible",
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceIsPrivateException.class)
    public ResponseEntity<?> resourceIsPrivateException(WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                "Resource is private",
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceMarkedAsDeletedException.class)
    protected ResponseEntity<MyGlobalExceptionHandler> handleDeleteException() {
        return new ResponseEntity<>(new MyGlobalExceptionHandler("This user was deleted"),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex,
                                                       WebRequest request) {
        ErrorDetails errorDetails =
                new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    //--------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandler(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //--------------------------------------------
    @Data
    @AllArgsConstructor
    private static class MyGlobalExceptionHandler {
        private String message;
    }
}
