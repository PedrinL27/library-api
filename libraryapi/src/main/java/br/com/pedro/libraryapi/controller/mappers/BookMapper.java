package br.com.pedro.libraryapi.controller.mappers;

import br.com.pedro.libraryapi.controller.dto.FindResultBookDTO;
import br.com.pedro.libraryapi.controller.dto.SaveBookDTO;
import br.com.pedro.libraryapi.model.Book;
import br.com.pedro.libraryapi.repository.AuthorRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = AuthorMapper.class)
public abstract class BookMapper {

    @Autowired
    AuthorRepository authorRepository;

    @Mapping(target = "author", expression = "java( authorRepository.findById(dto.authorId()).orElse(null) )")
    public abstract Book toEntity(SaveBookDTO dto);

    public abstract FindResultBookDTO toDTO(Book book);
}
