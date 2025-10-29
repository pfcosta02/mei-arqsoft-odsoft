package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoDB;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.AuthorMapperMongoDB;

import java.util.*;

@Profile("mongodb")
@Qualifier("mongoDbRepo")
@Repository
@RequiredArgsConstructor
public class AuthorRepositoryMongoDBImpl implements AuthorRepository {

    private final SpringDataAuthorRepositoryMongoDB authoRepo;
    private final AuthorMapperMongoDB authorEntityMapper;


    @Override
    @Cacheable(value = "authors", key = "#authorNumber")
    public Optional<Author> findByAuthorNumber(String authorNumber)
    {
        Optional<AuthorMongoDB> entityOpt = authoRepo.findByAuthorNumber(authorNumber);
        if (entityOpt.isPresent())
        {
            return Optional.of(authorEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    @Cacheable(value = "authors", key = "#name")
    public List<Author> searchByNameNameStartsWith(String name)
    {
        List<Author> authors = new ArrayList<>();
        for (AuthorMongoDB a: authoRepo.searchByNameNameStartsWith(name))
        {
            authors.add(authorEntityMapper.toModel(a));
        }

        return authors;
    }

    @Override
    @Cacheable(value = "authors", key = "#name")
    public List<Author> searchByNameName(String name)
    {
        List<Author> authors = new ArrayList<>();
        for (AuthorMongoDB a: authoRepo.searchByNameName(name))
        {
            authors.add(authorEntityMapper.toModel(a));
        }

        return authors;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = "authors", key = "#author.name.name"),
            @CacheEvict(cacheNames = "authors", allEntries = true, condition = "#author.authorNumber == null"),
            @CacheEvict(cacheNames = "topAuthors", allEntries = true)
    })
    public Author save(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        var entity = authorEntityMapper.toMongoDB(author);
        var savedEntity = authoRepo.save(entity);
        return authorEntityMapper.toModel(savedEntity);
    }


    @Override
    @Cacheable(value = "authors", key = "'all'")
    public Iterable<Author> findAll()
    {
        List<Author> authors = new ArrayList<>();
        for (AuthorMongoDB a: authoRepo.findAll())
        {
            authors.add(authorEntityMapper.toModel(a));
        }

        return authors;
    }

    @Override
    @Cacheable(
            cacheNames = "topAuthors",
            key = "'page:' + #pageableRules.pageNumber + ':size:' + #pageableRules.pageSize"
    )
    public List<AuthorLendingView> findTopAuthorByLendings (Pageable pageableRules)
    {
        return authoRepo.findTopAuthorByLendings(pageableRules);
    }

    @Override
    public void delete(Author author)
    {
        authoRepo.delete(authorEntityMapper.toMongoDB(author));
    }

    @Override
    @Cacheable(value = "authors", key = "#authorNumber")
    public List<Author> findCoAuthorsByAuthorNumber(String authorNumber)
    {
        List<Author> authors = new ArrayList<>();
        for (AuthorMongoDB a: authoRepo.findCoAuthorsByAuthorNumber(authorNumber))
        {
            authors.add(authorEntityMapper.toModel(a));
        }

        return authors;
    }
}