package br.com.pedro.libraryapi.controller.mappers;

import br.com.pedro.libraryapi.controller.dto.ClientDTO;
import br.com.pedro.libraryapi.model.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    Client toEntity(ClientDTO dto);
}
