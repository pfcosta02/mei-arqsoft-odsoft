package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.IsbnEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataBookRepository extends CrudRepository<BookEntity, IsbnEntity> {

    @Query("SELECT b " +
            "FROM BookEntity b " +
            "WHERE b.isbn.isbn = :isbn")
    Optional<BookEntity> findByIsbn(@Param("isbn") String isbn);

    @Query("SELECT new pt.psoft.g1.psoftg1.bookmanagement.services.BookCountDTO(b, COUNT(l)) " +
            "FROM BookEntity b " +
            "JOIN LendingEntity l ON l.book = b " +
            "WHERE l.startDate > :oneYearAgo " +
            "GROUP BY b " +
            "ORDER BY COUNT(l) DESC")
    Page<BookCountDTO> findTop5BooksLent(@Param("oneYearAgo") LocalDate oneYearAgo, Pageable pageable);




}

