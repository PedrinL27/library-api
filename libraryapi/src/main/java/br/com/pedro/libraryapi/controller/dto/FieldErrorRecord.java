package br.com.pedro.libraryapi.controller.dto;

public record FieldErrorRecord(
        String field,
        String errorMsg) {
}
