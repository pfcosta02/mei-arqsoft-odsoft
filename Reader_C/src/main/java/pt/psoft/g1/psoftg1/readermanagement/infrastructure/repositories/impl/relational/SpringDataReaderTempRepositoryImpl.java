package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderTempEntity;

import java.util.Optional;

public interface SpringDataReaderTempRepositoryImpl extends CrudRepository<ReaderTempEntity, String> {
    @Query("SELECT r FROM ReaderTempEntity r where r.readerId = :readerId")
    Optional<ReaderTempEntity> findByReaderId(@Param("readerId") @NotNull String readerId);

    @Query("SELECT r FROM ReaderTempEntity r where r.email = :email")
    Optional<ReaderTempEntity> findByEmail(@Param("email") @NotNull String email);
}