package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.relational;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreEntityMapper;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.relational.SpringDataGenreRepository;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mappers.ForbiddenNameEntityMapper;
import pt.psoft.g1.psoftg1.shared.model.ForbiddenName;
import pt.psoft.g1.psoftg1.shared.model.relational.ForbiddenNameEntity;
import pt.psoft.g1.psoftg1.shared.repositories.ForbiddenNameRepository;

@Profile("jpa")
@Primary
@RequiredArgsConstructor
@Repository
public class ForbiddenNameRepositoryRelationalImpl implements ForbiddenNameRepository
{
    private final SpringDataForbiddenNameRepository forbiddenNameRepository;
    private final ForbiddenNameEntityMapper forbiddenNameEntityMapper;

    @Override
    public Iterable<ForbiddenName> findAll()
    {
        List<ForbiddenName> fn = new ArrayList<>();
        for (ForbiddenNameEntity f: forbiddenNameRepository.findAll())
        {
            fn.add(forbiddenNameEntityMapper.toModel(f));
        }

        return fn;
    }

    @Override
    public List<ForbiddenName> findByForbiddenNameIsContained(String pat)
    {
        List<ForbiddenName> fn = new ArrayList<>();
        for (ForbiddenNameEntity f: forbiddenNameRepository.findByForbiddenNameIsContained(pat))
        {
            fn.add(forbiddenNameEntityMapper.toModel(f));
        }

        return fn;
    }

    @Override
    public ForbiddenName save(ForbiddenName forbiddenName)
    {
        return forbiddenNameEntityMapper.toModel(forbiddenNameRepository.save(forbiddenNameEntityMapper.toEntity(forbiddenName)));
    }

    @Override
    public Optional<ForbiddenName> findByForbiddenName(String forbiddenName)
    {
        Optional<ForbiddenNameEntity> entityOpt = forbiddenNameRepository.findByForbiddenName(forbiddenName);
        if (entityOpt.isPresent())
        {
            return Optional.of(forbiddenNameEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public int deleteForbiddenName(String forbiddenName)
    {
        return forbiddenNameRepository.deleteForbiddenName(forbiddenName);
    }

}
