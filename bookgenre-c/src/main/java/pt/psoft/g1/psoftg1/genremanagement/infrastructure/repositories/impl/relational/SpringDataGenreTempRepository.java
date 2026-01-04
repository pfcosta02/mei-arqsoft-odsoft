package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.relational;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreTempEntity;

import java.util.Optional;

public interface SpringDataGenreTempRepository extends CrudRepository<GenreTempEntity, String> {

    @Query("SELECT g FROM GenreTempEntity g where g.pk = :pk")
    Optional<GenreTempEntity> findByPk(@Param("pk") @NotNull String pk);
}
