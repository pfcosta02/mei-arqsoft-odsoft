package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookTempEntity;

import java.util.Optional;

public interface SpringDataBookTempRepository extends CrudRepository<BookTempEntity, Long> {

    @Query("SELECT b FROM BookTempEntity b where b.isbn.isbn = :isbn")
    Optional<BookTempEntity> findByIsbn(@Param("isbn") @NotNull String isbn);
}
