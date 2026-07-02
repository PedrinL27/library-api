package br.com.pedro.libraryapi.controller.mappers;

import br.com.pedro.libraryapi.controller.dto.UserDTO;
import br.com.pedro.libraryapi.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserDTO dto);
}
