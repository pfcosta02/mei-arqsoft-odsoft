package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderEntity;

import java.util.List;
import java.util.Optional;

public interface SpringDataReaderRepositoryImpl extends CrudRepository<ReaderEntity, String> {
    @Query("SELECT r FROM ReaderEntity r where r.email = :email")
    Optional<ReaderEntity> findByEmail(@Param("email") @NotNull String email);

    @Query("SELECT r FROM ReaderEntity r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :namePart, '%'))")
    List<ReaderEntity> searchByName(@Param("namePart") String namePart);
}
