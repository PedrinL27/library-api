package br.com.pedro.libraryapi.controller;

import br.com.pedro.libraryapi.controller.dto.AuthorDTO;
import br.com.pedro.libraryapi.controller.mappers.AuthorMapper;
import br.com.pedro.libraryapi.model.Author;
import br.com.pedro.libraryapi.model.User;
import br.com.pedro.libraryapi.service.AuthorService;
import br.com.pedro.libraryapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Tag(name = "Authors")
@Slf4j
public class AuthorController implements GenericController {

    private final AuthorService service;
    private final AuthorMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(summary = "Save", description = "Save new author")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Saved"),
            @ApiResponse(responseCode = "422", description = "Validation Error"),
            @ApiResponse(responseCode = "409", description = "Duplicate Entry")
    })
    public ResponseEntity<Void> save(@RequestBody @Valid AuthorDTO dto) {
        Author author = mapper.toEntity(dto);
        service.save(author);

        log.debug("Saving new author: {}", author.getName());

        URI location = generateHeaderLocation(author.getId());
        return ResponseEntity.created(location).build();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER')")
    @Operation(summary = "Find By Id", description = "Return author data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Found author"),
            @ApiResponse(responseCode = "404", description = "Author not found")
    })
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
    @Operation(summary = "Delete", description = "Delete a existing author")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Delete with success"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "400", description = "Author has registered books")
    })
    public ResponseEntity<Void> delete(@PathVariable String id) {
        UUID authorId = UUID.fromString(id);
        Optional<Author> authorOptional = service.findById(authorId);

        if (authorOptional.isEmpty()) {
            log.debug("Cannot find author with this id: {}", id);
            return ResponseEntity.notFound().build();
        }
        service.delete(authorOptional.get());
        log.debug("Deleting author with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATOR', 'MANAGER')")
    @Operation(summary = "Search", description = "Search a existing author with params")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search authors"),
    })
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
    @Operation(summary = "Update", description = "Update author")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Updated with success"),
            @ApiResponse(responseCode = "404", description = "Author not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate Entry")
    })
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
