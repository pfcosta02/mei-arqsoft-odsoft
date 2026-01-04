package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.relational;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;

import java.util.*;

public interface SpringDataGenreRepository extends CrudRepository<GenreEntity, Integer> {

    @Query("SELECT g FROM GenreEntity g")
    List<GenreEntity> findAllGenres();

    @Query("SELECT g FROM GenreEntity g WHERE g.genre = :genreName" )
    Optional<GenreEntity> findByString(@Param("genreName")@NotNull String genre);

    @Query("SELECT new pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO(g.genre, COUNT(b))" +
            "FROM GenreEntity g " +
            "JOIN BookEntity b ON b.genre.pk = g.pk " +
            "GROUP BY g " +
            "ORDER BY COUNT(b) DESC")
    Page<GenreBookCountDTO> findTop5GenreByBookCount(Pageable pageable);
}
