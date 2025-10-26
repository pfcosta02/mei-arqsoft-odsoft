package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.domain.Sort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import pt.psoft.g1.psoftg1.shared.services.Page;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserMapperMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.LibrarianMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.ReaderMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.UserMongoDB;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;
import pt.psoft.g1.psoftg1.usermanagement.services.SearchUsersQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Profile("mongodb")
@Primary
@RequiredArgsConstructor
@Repository
public class UserRepositoryMongoDBImpl implements UserRepository {

    private final SpringDataUserRepositoryMongoDB userRepo;
    private final UserMapperMongoDB userMapperMongoDB;
    private final MongoTemplate mongoTemplate;

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities)
    {
        List<S> savedEntities = new ArrayList<>();

        List<UserMongoDB> userEntitiesToSave = new ArrayList<>();

        for (S entity : entities) {
            userEntitiesToSave.add(userMapperMongoDB.toEntity(entity));
        }

        for (UserMongoDB userEntity : userRepo.saveAll(userEntitiesToSave)) {
            savedEntities.add((S) userMapperMongoDB.toModel(userEntity));
        }


        return savedEntities;
    }

    @Override
    public <S extends User> S save(S entity)
    {
        if (entity instanceof Reader) {
            ReaderMongoDB readerEntity = userMapperMongoDB.toEntity((Reader) entity);
            ReaderMongoDB savedEntity = userRepo.save(readerEntity);
            return (S) userMapperMongoDB.toModel(savedEntity);

        } else if (entity instanceof Librarian) {
            LibrarianMongoDB librarianEntity = userMapperMongoDB.toEntity((Librarian) entity);
            LibrarianMongoDB savedEntity = userRepo.save(librarianEntity);
            return (S) userMapperMongoDB.toModel(savedEntity);

        } else if (entity instanceof User) {
            UserMongoDB userEntity = userMapperMongoDB.toEntity(entity);
            UserMongoDB savedEntity = userRepo.save(userEntity);
            return (S) userMapperMongoDB.toModel(savedEntity);
        }

        throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass().getName());
    }

    @Override
    public Optional<User> findById(String objectId)
    {
        Optional<UserMongoDB> entityOpt = userRepo.findById(objectId);
        if (entityOpt.isPresent())
        {
            return Optional.of(userMapperMongoDB.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username)
    {
        Optional<UserMongoDB> entityOpt = userRepo.findByUsername(username);
        if (entityOpt.isPresent())
        {
            return Optional.of(userMapperMongoDB.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public List<User> searchUsers(Page page, SearchUsersQuery query) {
        Query mongoQuery = new Query();
        List<Criteria> orCriteria = new ArrayList<>();

        // username (exact match)
        if (StringUtils.hasText(query.getUsername())) {
            orCriteria.add(Criteria.where("username").is(query.getUsername()));
        }

        // fullName (contains / case-insensitive)
        if (StringUtils.hasText(query.getFullName())) {
            orCriteria.add(Criteria.where("fullName").regex(query.getFullName(), "i"));
        }

        // Combine OR conditions
        if (!orCriteria.isEmpty()) {
            mongoQuery.addCriteria(new Criteria().orOperator(orCriteria.toArray(new Criteria[0])));
        }

        // Sort by createdAt DESC
        mongoQuery.with(Sort.by(Sort.Direction.DESC, "createdAt"));

        // Pagination
        mongoQuery.skip((long) (page.getNumber() - 1) * page.getLimit());
        mongoQuery.limit(page.getLimit());

        // Execute query
        List<UserMongoDB> results = mongoTemplate.find(mongoQuery, UserMongoDB.class);

        // Map to domain model
        return results.stream()
                .map(userMapperMongoDB::toModel)
                .toList();
    }

    @Override
    public List<User> findByNameName(String name)
    {
        List<User> users = new ArrayList<>();
        for (UserMongoDB r: userRepo.findByNameName(name))
        {
            users.add(userMapperMongoDB.toModel(r));
        }

        return users;
    }

    @Override
    public List<User> findByNameNameContains(String name)
    {
        return null;
    }

    @Override
    public void delete(User user)
    {

    }
}