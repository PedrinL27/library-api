package br.com.pedro.libraryapi.validator;

import br.com.pedro.libraryapi.exceptions.DuplicateEntryException;
import br.com.pedro.libraryapi.model.Author;
import br.com.pedro.libraryapi.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthorValidator {

    @Autowired
    private AuthorRepository repository;

    public void validate(Author author) {
        if (existsAuthorEntry(author)) {
            throw new DuplicateEntryException("Author already exists.");
        }
    }

    private boolean existsAuthorEntry(Author author) {
        Optional<Author> result = repository.findByNameAndBirthDateAndNationality(
                author.getName(),
                author.getBirthDate(),
                author.getNationality()
        );

        if (author.getId() == null){
            return result.isPresent();
        }
        return result.filter(value -> !author.getId().equals(value.getId())).isPresent();
    }
}
