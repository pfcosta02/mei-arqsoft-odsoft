package pt.psoft.g1.psoftg1.genremanagement.repositories;

import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreTempEntity;

import java.util.Optional;

public interface GenreTempRepository {
    GenreTempEntity save(GenreTempEntity genreTempEntity);
    Optional<GenreTempEntity> findByPk(String pk);
}
