package br.com.pedro.libraryapi.controller;

import br.com.pedro.libraryapi.controller.dto.ClientDTO;
import br.com.pedro.libraryapi.controller.mappers.ClientMapper;
import br.com.pedro.libraryapi.model.Client;
import br.com.pedro.libraryapi.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController implements GenericController{

    private final ClientService service;
    private final ClientMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> save(@RequestBody @Valid ClientDTO dto) {
        Client client = mapper.toEntity(dto);
        service.save(client);

        log.debug("Saving a new client with this id {} and with this scope {}", client.getClientId(), client.getScope());

        URI location = generateHeaderLocation(client.getId());
        return ResponseEntity.created(location).build();
    }
}
