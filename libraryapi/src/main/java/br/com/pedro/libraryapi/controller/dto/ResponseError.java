package br.com.pedro.libraryapi.controller.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ResponseError(
        int statusCode,
        String msg,
        List<FieldErrorRecord> errors) {

    public static ResponseError defaultResponse(String msg) {
        return new ResponseError(HttpStatus.BAD_REQUEST.value(), msg, List.of());
    }

    public static ResponseError conflictResponse(String msg) {
        return new ResponseError(HttpStatus.CONFLICT.value(), msg, List.of());
    }
}
