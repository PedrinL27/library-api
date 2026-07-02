package br.com.pedro.libraryapi.repository;

import br.com.pedro.libraryapi.model.Author;
import br.com.pedro.libraryapi.model.Book;
import br.com.pedro.libraryapi.model.BookGenre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookRepository
        extends JpaRepository<Book, UUID>, JpaSpecificationExecutor<Book> {
    // Query method
    List<Book> findByAuthor(Author author);

    boolean existsByAuthor(Author author);

    List<Book> findByTitleContaining(String title);

    @Query(" select b from Book as b order by b.title ")
    List<Book> listAllBooks();

    @Query(" select a from Book b join b.author a ")
    List<Author> listBooksAuthor();

    @Query(" select b from Book b where b.genre = :bookGenre")
    List<Book> findByGenre(@Param("bookGenre") BookGenre bookGenre);

    Optional<Book> findByIsbn(String isbn);

    @Modifying
    @Transactional
    @Query(" delete from Book where genre = ?1 ")
    void deleteByGenre(BookGenre bookGenre);
}
