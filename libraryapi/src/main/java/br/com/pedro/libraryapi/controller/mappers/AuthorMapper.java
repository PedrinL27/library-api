package br.com.pedro.libraryapi.controller.mappers;

import br.com.pedro.libraryapi.controller.dto.AuthorDTO;
import br.com.pedro.libraryapi.model.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    Author toEntity(AuthorDTO dto);
    AuthorDTO toDTO(Author author);
}
