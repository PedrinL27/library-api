package br.com.pedro.libraryapi.controller.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClientDTO(
        @NotBlank(message = "Required Field")
        @Size(min = 2, max = 150, message = "The field 'name' must be between 2 and 100 characters long.")
        String clientId,
        @NotBlank(message = "Required Field")
        @Size(min = 2, max = 400, message = "The field 'name' must be between 2 and 100 characters long.")
        String clientSecret,
        @NotBlank(message = "Required Field")
        @Size(min = 2, max = 200, message = "The field 'name' must be between 2 and 100 characters long.")
        String redirectURI,
        @Size(min = 2, max = 50, message = "The field 'name' must be between 2 and 100 characters long.")
        String scope) {
}
