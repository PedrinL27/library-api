package br.com.pedro.libraryapi.controller.dto;

import br.com.pedro.libraryapi.model.BookGenre;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.ISBN;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SaveBookDTO(
        @ISBN
        @NotBlank(message = "Required Field")
        String isbn,
        @NotBlank(message = "Required Field")
        String title,
        @NotNull(message = "Required Field")
        @Past(message = "Cannot be a future date")
        LocalDate publicationDate,
        BookGenre genre,
        BigDecimal price,
        @NotNull(message = "Required Field")
        UUID authorId
) {
}
