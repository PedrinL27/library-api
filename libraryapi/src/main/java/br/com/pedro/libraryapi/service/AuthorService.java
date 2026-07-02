package br.com.pedro.libraryapi.service;

import br.com.pedro.libraryapi.exceptions.OperationNotAllowedException;
import br.com.pedro.libraryapi.model.Author;
import br.com.pedro.libraryapi.model.User;
import br.com.pedro.libraryapi.repository.AuthorRepository;
import br.com.pedro.libraryapi.repository.BookRepository;
import br.com.pedro.libraryapi.security.SecurityService;
import br.com.pedro.libraryapi.validator.AuthorValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository repository;
    private final AuthorValidator validator;
    private final BookRepository bookRepository;
    private final SecurityService securityService;

    public void save(Author author) {
        validator.validate(author);
        User user = securityService.obtainLoggedUser();
        author.setUser(user);
        repository.save(author);
    }

    public void update(Author author) {
        if ( author.getId() == null ) {
            throw new IllegalArgumentException();
        }
        validator.validate(author);
        repository.save(author);
    }

    public Optional<Author> findById(UUID id) {
        return repository.findById(id);
    }

    public void delete(Author author) {
        if ( hasBooks(author) ) {
            throw new OperationNotAllowedException(
                    "The author cannot be deleted because they still have books registered."
            );
        }
        repository.delete(author);
    }

    public List<Author> findAuthorsByExample(
            String name,
            String nationality
    )  {
        Author author = new Author();
        author.setName(name);
        author.setNationality(nationality);

        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);

        Example<Author> authorExample = Example.of(author, matcher);
        return repository.findAll(authorExample);
    }

    private boolean hasBooks(Author author) {
        return bookRepository.existsByAuthor(author);
    }
}
