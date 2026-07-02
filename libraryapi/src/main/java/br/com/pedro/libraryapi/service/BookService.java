package br.com.pedro.libraryapi.service;

import br.com.pedro.libraryapi.model.Book;
import br.com.pedro.libraryapi.model.BookGenre;
import br.com.pedro.libraryapi.model.User;
import br.com.pedro.libraryapi.repository.BookRepository;
import br.com.pedro.libraryapi.security.SecurityService;
import br.com.pedro.libraryapi.validator.BookValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static br.com.pedro.libraryapi.repository.specs.BookSpecs.*;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository repository;
    private final BookValidator validator;
    private final SecurityService securityService;

    public Book save(Book book) {
        validator.validate(book);
        User user = securityService.obtainLoggedUser();
        book.setUser(user);
        return repository.save(book);
    }

    public Optional<Book> findById(UUID id) {
        return repository.findById(id);
    }

    public void delete(Book book) {
        repository.delete(book);
    }

    public Page<Book> findBooksBySpecification(
            String isbn,
            String title,
            String authorName,
            BookGenre genre,
            Integer publicationYear,
            Integer page,
            Integer pageSize
    ) {
        Specification<Book> specs = Specification.where(
                ((root, query, cb) -> cb.conjunction()));

        if(isbn != null) {
            specs = specs.and(isbnEqual(isbn));
        }
        if (title != null) {
            specs = specs.and(titleLike(title));
        }
        if (genre != null) {
            specs = specs.and(genreEqual(genre));
        }
        if (publicationYear != null) {
            specs = specs.and(publicationYearEqual(publicationYear));
        }
        if (authorName != null) {
            specs = specs.and(nameAuthorLike(authorName));
        }
        Pageable pageRequest = PageRequest.of(page, pageSize);
        return repository.findAll(specs, pageRequest);
    }

    public void update(Book book) {
        if ( book.getId() == null ) {
            throw new IllegalArgumentException();
        }
        validator.validate(book);
        repository.save(book);
    }
}
