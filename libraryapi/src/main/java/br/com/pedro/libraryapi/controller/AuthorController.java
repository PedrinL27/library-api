package br.com.pedro.libraryapi.controller;

import br.com.pedro.libraryapi.controller.dto.AuthorDTO;
import br.com.pedro.libraryapi.controller.mappers.AuthorMapper;
import br.com.pedro.libraryapi.model.Author;
import br.com.pedro.libraryapi.model.User;
import br.com.pedro.libraryapi.service.AuthorService;
import br.com.pedro.libraryapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorController implements GenericController {

    private final AuthorService service;
    private final AuthorMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> save(@RequestBody @Valid AuthorDTO dto) {
        Author author = mapper.toEntity(dto);
        service.save(author);
        URI location = generateHeaderLocation(author.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER')")
    public ResponseEntity<AuthorDTO> findById(@PathVariable String id) {
        UUID authorId = UUID.fromString(id);
        Optional<Author> authorOptional = service.findById(authorId);

        if (authorOptional.isPresent()) {
            Author author = authorOptional.get();
            AuthorDTO dto = mapper.toDTO(author);

            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        UUID authorId = UUID.fromString(id);
        Optional<Author> authorOptional = service.findById(authorId);

        if (authorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.delete(authorOptional.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER')")
    public ResponseEntity<List<AuthorDTO>> findAuthorsByNameAndNationality(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "nationality", required = false) String nationality
    ) {
        List<Author> result = service
                .findAuthorsByExample(name, nationality);
        List<AuthorDTO> list = result
                .stream()
                .map(mapper::toDTO)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> update(
            @PathVariable("id") String id,
            @RequestBody @Valid AuthorDTO dto
    ) {
        UUID authorId = UUID.fromString(id);
        Optional<Author> authorOptional = service.findById(authorId);

        if (authorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Author author = authorOptional.get();
        author.setName(dto.name());
        author.setBirthDate(dto.birthDate());
        author.setNationality(dto.nationality());

        service.update(author);
        return ResponseEntity.noContent().build();
    }
}
