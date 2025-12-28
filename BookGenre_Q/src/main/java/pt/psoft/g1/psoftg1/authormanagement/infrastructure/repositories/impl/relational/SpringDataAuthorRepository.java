package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;

import java.util.List;
import java.util.Optional;

public interface SpringDataAuthorRepository extends CrudRepository<AuthorEntity, String> {

    @Query("SELECT a FROM AuthorEntity a WHERE a.authorNumber = :authorNumber")
    Optional<AuthorEntity> findByAuthorNumber(String authorNumber);

    @Query("SELECT new pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView(\n" +
            "    a.name.name,\n" +
            "    COUNT(l.pk)\n" +
            ")\n" +
            "FROM AuthorEntity a\n" +
            "JOIN BookEntity b\n" +
            "JOIN b.authorNumbers an\n" +
            "JOIN LendingEntity l ON l.book.pk = b.pk\n" +
            "WHERE an = a.authorNumber\n" +
            "GROUP BY a.name.name\n" +
            "ORDER BY COUNT(l.pk) DESC")
    Page<AuthorLendingView> findTopAuthorByLendings(Pageable pageable);

    @Query("SELECT a FROM AuthorEntity a WHERE a.name.name = :name")
    List<AuthorEntity> searchByNameName(String name);

    @Query("SELECT a FROM AuthorEntity a WHERE a.name.name LIKE :name%")
    List<AuthorEntity> searchByNameNameStartsWith(String name);

    @Query("SELECT a FROM AuthorEntity a")
    Iterable<AuthorEntity> findAll();
}



