package br.com.pedro.libraryapi.controller.dto;

import br.com.pedro.libraryapi.model.BookGenre;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record FindResultBookDTO(
        UUID id,
        String isbn,
        String title,
        LocalDate publicationDate,
        BookGenre genre,
        BigDecimal price,
        AuthorDTO author
) {
    
}
