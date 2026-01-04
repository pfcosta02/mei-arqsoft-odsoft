package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.relational;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.relational.SpringDataBookTempRepository;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreTempEntity;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreTempRepository;

import java.util.Optional;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class GenreTempRepositoryImpl implements GenreTempRepository {

    private final SpringDataGenreTempRepository genreTempRepository;

    @Override
    public GenreTempEntity save(GenreTempEntity genreTempEntity) {
        return genreTempRepository.save(genreTempEntity);
    }

    @Override
    public Optional<GenreTempEntity> findByPk(String pk) {
        return genreTempRepository.findByPk(pk);
    }
}
