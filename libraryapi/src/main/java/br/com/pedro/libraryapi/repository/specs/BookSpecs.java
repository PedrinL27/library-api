package br.com.pedro.libraryapi.repository.specs;

import br.com.pedro.libraryapi.model.Book;
import br.com.pedro.libraryapi.model.BookGenre;
import org.springframework.data.jpa.domain.Specification;


public class BookSpecs {

    public static Specification<Book> isbnEqual(String isbn) {
        return  (root, query, cb) ->
                cb.equal(root.get("isbn"), isbn);
    }

    public static Specification<Book> titleLike(String title) {
        return  (root, query, cb) ->
                cb.like(cb.upper(root.get("title")), "%" + title.toUpperCase() + "%");
    }

    public static Specification<Book> genreEqual(BookGenre genre) {
        return (root, query, cb) ->
                cb.equal(root.get("genre"), genre);
    }

    public static Specification<Book> publicationYearEqual(Integer year) {
        return (root, query, cb) ->
                cb.equal( cb.function("to_char", String.class,
                        root.get("publicationDate"), cb.literal("YYYY")), year.toString());
    }

    public static Specification<Book> nameAuthorLike(String name) {
        return (root, query, cb) ->
                cb.like(cb.upper(root.get("author").get("name")), "%" + name.toUpperCase() + "%");
    }
}
