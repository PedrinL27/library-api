package br.com.pedro.libraryapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UserDTO(
        @NotBlank(message = "Required Field")
        String login,
        @NotBlank(message = "Required Field")
        String password,
        @NotBlank(message = "Required Field")
        @Email
        String email,
        List<String> roles) {
}
