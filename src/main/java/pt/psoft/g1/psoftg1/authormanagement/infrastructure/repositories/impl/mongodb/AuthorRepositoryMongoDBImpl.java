package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public Author save(Author author) {
        if (author == null) {
            throw new IllegalArgumentException("Author cannot be null");
        }

        var entity = authorEntityMapper.toMongoDB(author);
        var savedEntity = authoRepo.save(entity);
        return authorEntityMapper.toModel(savedEntity);
    }


    @Override
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