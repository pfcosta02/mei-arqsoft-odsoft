package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderEntity;

import java.util.Optional;

public interface SpringDataReaderRepositoryImpl extends CrudRepository<ReaderEntity, String> {
    @Query("SELECT r FROM ReaderEntity r where r.email = :email")
    Optional<ReaderEntity> findByEmail(@Param("email") @NotNull String email);
}