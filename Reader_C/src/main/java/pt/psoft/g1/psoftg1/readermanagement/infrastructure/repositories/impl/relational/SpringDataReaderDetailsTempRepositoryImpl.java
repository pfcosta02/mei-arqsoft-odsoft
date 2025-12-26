package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsTempEntity;

import java.util.Optional;

public interface SpringDataReaderDetailsTempRepositoryImpl extends CrudRepository<ReaderDetailsTempEntity, String> {
    @Query("SELECT r FROM ReaderDetailsTempEntity r LEFT JOIN FETCH r.interestList WHERE r.reader.readerId = :readerId")
    Optional<ReaderDetailsTempEntity> findByReaderId(@Param("readerId") @NotNull String readerId);
}