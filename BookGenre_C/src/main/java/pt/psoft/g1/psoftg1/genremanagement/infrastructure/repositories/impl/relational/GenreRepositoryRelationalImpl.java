package pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.relational;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.persistence.criteria.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.BookEntity;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.genremanagement.infrastructure.repositories.impl.mappers.GenreEntityMapper;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsDTO;
import pt.psoft.g1.psoftg1.genremanagement.services.GenreLendingsPerMonthDTO;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class GenreRepositoryRelationalImpl implements GenreRepository
{
    private final SpringDataGenreRepository genreRepo;
    private final GenreEntityMapper genreEntityMapper;
    private final EntityManager entityManager;

    @Override
    public Iterable<Genre> findAll()
    {
        List<Genre> genres = new ArrayList<>();
        for (GenreEntity g: genreRepo.findAll())
        {
            genres.add(genreEntityMapper.toModel(g));
        }

        return genres;
    }

    @Override
    public Optional<Genre> findByString(String genreName)
    {
        Optional<GenreEntity> entityOpt = genreRepo.findByString(genreName);
        if (entityOpt.isPresent())
        {
            return Optional.of(genreEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Genre save(Genre genre)
    {
        GenreEntity entity = genreEntityMapper.toEntity(genre);
        return genreEntityMapper.toModel(genreRepo.save(entity));
    }

    @Override
    public Page<GenreBookCountDTO> findTop5GenreByBookCount(Pageable pageable)
    {
        return genreRepo.findTop5GenreByBookCount(pageable);
    }

    @Override
    public void delete(Genre genre)
    {
        genreRepo.delete(genreEntityMapper.toEntity(genre));
    }
}