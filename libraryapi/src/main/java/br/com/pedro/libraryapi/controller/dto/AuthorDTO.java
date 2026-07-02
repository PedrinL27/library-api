package br.com.pedro.libraryapi.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record AuthorDTO(
        UUID id,
        @NotBlank(message = "Required Field")
        @Size(min = 2, max = 100, message = "The field 'name' must be between 2 and 100 characters long.")
        String name,
        @NotNull(message = "Required Field")
        @Past(message = "Cannot be a future date.")
        LocalDate birthDate,
        @NotBlank(message = "Required Field")
        @Size(min = 2, max = 50, message = "The field 'nationality' must be between 2 and 50 characters long.")
        String nationality) {}
