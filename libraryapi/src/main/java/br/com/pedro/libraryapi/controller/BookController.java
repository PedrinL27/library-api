package br.com.pedro.libraryapi.controller;

import br.com.pedro.libraryapi.controller.dto.FindResultBookDTO;
import br.com.pedro.libraryapi.controller.dto.SaveBookDTO;
import br.com.pedro.libraryapi.controller.mappers.BookMapper;
import br.com.pedro.libraryapi.model.Book;
import br.com.pedro.libraryapi.model.BookGenre;
import br.com.pedro.libraryapi.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("books")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER')")
public class BookController implements GenericController {

    private final BookService service;
    private final BookMapper mapper;

    @PostMapping
    public ResponseEntity<Void> save(
            @RequestBody
            @Valid
            SaveBookDTO dto) {
        Book book = mapper.toEntity(dto);
        service.save(book);
        URI location = generateHeaderLocation(book.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<FindResultBookDTO> findById(
            @PathVariable("id") String id
    ) {
        return service.findById(UUID.fromString(id))
                .map(book ->
                        ResponseEntity.ok(mapper.toDTO(book))
                ).orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<Page<FindResultBookDTO>> findAll(
            @RequestParam(value = "isbn", required = false)
            String isbn,
            @RequestParam(value = "title", required = false)
            String title,
            @RequestParam(value = "author-name", required = false)
            String authorName,
            @RequestParam(value = "genre", required = false)
            BookGenre genre,
            @RequestParam(value = "publication-year", required = false)
            Integer publicationYear,
            @RequestParam(value = "page", defaultValue = "0")
            Integer page,
            @RequestParam(value = "page-size", defaultValue = "5")
            Integer pageSize
    ) {
        var resultPage = service.findBooksBySpecification(
                isbn, title, authorName, genre, publicationYear, page, pageSize);

        Page<FindResultBookDTO> result = resultPage.map(mapper::toDTO);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") String id) {
        return service.findById(UUID.fromString(id))
                .map(book -> {
                    service.delete(book);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> update(
            @PathVariable String id,
            @RequestBody @Valid SaveBookDTO dto) {

        Optional<Book> optionalBook =
                service.findById(UUID.fromString(id));

        if (optionalBook.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Book book = optionalBook.get();

        Book auxEntity = mapper.toEntity(dto);

        book.setIsbn(auxEntity.getIsbn());
        book.setTitle(auxEntity.getTitle());
        book.setPrice(auxEntity.getPrice());
        book.setGenre(auxEntity.getGenre());
        book.setPublicationDate(auxEntity.getPublicationDate());
        book.setAuthor(auxEntity.getAuthor());

        service.update(book);

        return ResponseEntity.ok().build();
    }
}
