package br.com.pedro.libraryapi.validator;

import br.com.pedro.libraryapi.exceptions.DuplicateEntryException;
import br.com.pedro.libraryapi.exceptions.InvalidFieldException;
import br.com.pedro.libraryapi.model.Book;
import br.com.pedro.libraryapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BookValidator {

    private final BookRepository repository;

    private static final int FINAL_YEAR = 2020;

    public void validate(Book book) {
        if (hasBookWithSameIsbn(book)) {
            throw new DuplicateEntryException("ISBN already exists.");
        }
        if (requiredPriceIsNull(book)) {
            throw new InvalidFieldException("price", "For books wih published year after 2020 the field price is required");
        }
    }

    private boolean requiredPriceIsNull(Book book) {
        return book.getPrice() == null &&
                book.getPublicationDate().getYear() >= FINAL_YEAR;
    }

    private boolean hasBookWithSameIsbn(Book book) {
        Optional<Book> result = repository.findByIsbn(book.getIsbn());
        if (book.getId() == null) {
            return result.isPresent();
        }
        return result.filter(value -> !book.getId().equals(value.getId())).isPresent();
    }


}
