package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.mappers.AuthorEntityMapper;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.redis.AuthorRepositoryRedisImpl;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.repositories.impl.redis.BookRepositoryRedisImpl;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class AuthorRepositoryRelationalImpl implements AuthorRepository
{
    private final SpringDataAuthorRepository authoRepo;
    private final AuthorEntityMapper authorEntityMapper;
    private final AuthorRepositoryRedisImpl redisRepo;

    private static final String PREFIX = "authors:";

    @Override
    public Optional<Author> findByAuthorNumber(String authorNumber)
    {
        Optional<Author> cached = redisRepo.getAuthorFromRedis(PREFIX + "author:" + authorNumber);
        if (cached.isPresent()) return cached;

        Optional<AuthorEntity> entityOpt = authoRepo.findByAuthorNumber(authorNumber);
        if (entityOpt.isPresent())
        {
            Author author = authorEntityMapper.toModel(entityOpt.get());
            redisRepo.save(author);
            return Optional.of(author);
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public List<Author> searchByNameNameStartsWith(String name)
    {
        List<Author> cached = redisRepo.getAuthorListFromRedis(PREFIX + "authorNameStartsWith:" + name);
        if (!cached.isEmpty()) return cached;

        List<Author> authors = new ArrayList<>();
        for (AuthorEntity a: authoRepo.searchByNameNameStartsWith(name))
        {
            authors.add(authorEntityMapper.toModel(a));
        }

        redisRepo.cacheAuthorListToRedis(PREFIX + "authorNameStartsWith:" + name, authors);

        return authors;
    }

    @Override
    public List<Author> searchByNameName(String name)
    {
        List<Author> cached = redisRepo.getAuthorListFromRedis(PREFIX + "authorName:" + name);
        if (!cached.isEmpty()) return cached;

        List<Author> authors = new ArrayList<>();
        for (AuthorEntity a: authoRepo.searchByNameName(name))
        {
            authors.add(authorEntityMapper.toModel(a));
        }

        redisRepo.cacheAuthorListToRedis(PREFIX + "authorName:" + name, authors);

        return authors;
    }

    @Override
    public Author save(Author author)
    {
        Author saved = authorEntityMapper.toModel(authoRepo.save(authorEntityMapper.toEntity(author)));
        redisRepo.save(saved);
        return saved;
    }

    @Override
    public Iterable<Author> findAll()
    {
        List<Author> authors = new ArrayList<>();
        for (AuthorEntity a: authoRepo.findAll())
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
        authoRepo.delete(authorEntityMapper.toEntity(author));
    }

    @Override
    public List<Author> findCoAuthorsByAuthorNumber(String authorNumber)
    {
        List<Author> cached = redisRepo.getAuthorListFromRedis(PREFIX + "coAuthors:" + authorNumber);
        if (!cached.isEmpty()) return cached;

        List<Author> authors = new ArrayList<>();
        for (AuthorEntity a: authoRepo.findCoAuthorsByAuthorNumber(authorNumber))
        {
            authors.add(authorEntityMapper.toModel(a));
        }

        redisRepo.cacheAuthorListToRedis(PREFIX + "coAuthors:" + authorNumber, authors);

        return authors;
    }
}