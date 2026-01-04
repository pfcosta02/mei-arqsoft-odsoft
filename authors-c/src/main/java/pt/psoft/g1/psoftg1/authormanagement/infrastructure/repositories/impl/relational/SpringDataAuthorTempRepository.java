package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorTempEntity;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorTempRepository;

import java.util.Optional;

public interface SpringDataAuthorTempRepository extends CrudRepository<AuthorTempEntity, String> {

    @Query("SELECT a FROM AuthorTempEntity a where a.authorNumber = :authorNumber")
    Optional<AuthorTempEntity> findByAuthorNumber(@Param("authorNumber") @NotNull String authorNumber);
}
