package br.com.pedro.libraryapi.controller.common;

import br.com.pedro.libraryapi.controller.dto.FieldErrorRecord;
import br.com.pedro.libraryapi.controller.dto.ResponseError;
import br.com.pedro.libraryapi.exceptions.DuplicateEntryException;
import br.com.pedro.libraryapi.exceptions.InvalidFieldException;
import br.com.pedro.libraryapi.exceptions.OperationNotAllowedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ResponseError handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        List<FieldError> fieldError = e.getFieldErrors();

        List<FieldErrorRecord> errorRecordList = fieldError.stream().map(fe ->
                new FieldErrorRecord(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return new ResponseError(HttpStatus.UNPROCESSABLE_CONTENT.value(),
                "Validation Error",
                errorRecordList);
    }

    @ExceptionHandler(DuplicateEntryException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseError handleDuplicateEntryException(
            DuplicateEntryException e) {
        return ResponseError.conflictResponse(e.getMessage());
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleOperationNotAllowedException(
            OperationNotAllowedException e) {
        return ResponseError.defaultResponse(e.getMessage());
    }

    @ExceptionHandler(InvalidFieldException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_CONTENT)
    public ResponseError handleInvalidFieldException(
            InvalidFieldException e
    ) {
        return new ResponseError(
                HttpStatus.UNPROCESSABLE_CONTENT.value(),
                "Validation error",
                List.of(new FieldErrorRecord(e.getField(), e.getMessage()))
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseError handleAccessDeniedException(AccessDeniedException e) {
        return new ResponseError(HttpStatus.FORBIDDEN.value(), "Access denied", List.of());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseError handleOtherExceptions(RuntimeException e) {
        return new ResponseError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error." + 
                        " The server encountered an internal error or misconfiguration and was unable to complete your request.",
                List.of());
    }
}
