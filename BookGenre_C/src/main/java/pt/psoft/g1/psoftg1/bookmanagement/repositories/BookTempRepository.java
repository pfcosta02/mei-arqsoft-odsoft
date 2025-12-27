package pt.psoft.g1.psoftg1.bookmanagement.repositories;

import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookTempEntity;

import java.util.Optional;

public interface BookTempRepository {
    BookTempEntity save(BookTempEntity temp);
    Optional<BookTempEntity> findByIsbn(String isbn);
}
