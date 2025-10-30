package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.relational;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import jakarta.persistence.criteria.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.shared.services.Page;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserEntityMapper;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.LibrarianEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.ReaderEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserEntity;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;
import pt.psoft.g1.psoftg1.usermanagement.services.SearchUsersQuery;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
@Component
public class UserRepositoryRelationalImpl implements UserRepository
{
    private final SpringDataUserRepository userRepo;
    private final UserEntityMapper userEntityMapper;
    private final EntityManager em;

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities)
    {
        List<S> savedEntities = new ArrayList<>();

        List<UserEntity> userEntitiesToSave = new ArrayList<>();

        for (S entity : entities) {
            userEntitiesToSave.add(userEntityMapper.toEntity(entity));
        }

        for (UserEntity userEntity : userRepo.saveAll(userEntitiesToSave)) {
            savedEntities.add((S) userEntityMapper.toModel(userEntity));
        }


        return savedEntities;
    }

    @Override
    public <S extends User> S save(S entity)
    {
        if (entity instanceof Reader) {
            ReaderEntity readerEntity = userEntityMapper.toEntity((Reader) entity);
            ReaderEntity savedEntity = userRepo.save(readerEntity);
            return (S) userEntityMapper.toModel(savedEntity);

        } else if (entity instanceof Librarian) {
            LibrarianEntity librarianEntity = userEntityMapper.toEntity((Librarian) entity);
            LibrarianEntity savedEntity = userRepo.save(librarianEntity);
            return (S) userEntityMapper.toModel(savedEntity);

        } else if (entity instanceof User) {
            UserEntity userEntity = userEntityMapper.toEntity(entity);
            UserEntity savedEntity = userRepo.save(userEntity);
            return (S) userEntityMapper.toModel(savedEntity);
        }

        throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass().getName());
    }

    @Override
    public Optional<User> findById(String objectId)
    {
        Optional<UserEntity> entityOpt = userRepo.findById(objectId);
        if (entityOpt.isPresent())
        {
            return Optional.of(userEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username)
    {
        Optional<UserEntity> entityOpt = userRepo.findByUsername(username);
        if (entityOpt.isPresent())
        {
            return Optional.of(userEntityMapper.toModel(entityOpt.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public List<User> searchUsers(Page page, SearchUsersQuery query)
    {
        final CriteriaBuilder cb = em.getCriteriaBuilder();
        final CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);
        final Root<UserEntity> root = cq.from(UserEntity.class);
        cq.select(root);

        final List<Predicate> where = new ArrayList<>();
        if (StringUtils.hasText(query.getUsername())) {
            where.add(cb.equal(root.get("username"), query.getUsername()));
        }
        if (StringUtils.hasText(query.getFullName())) {
            where.add(cb.like(root.get("fullName"), "%" + query.getFullName() + "%"));
        }

        // search using OR
        if (!where.isEmpty()) {
            cq.where(cb.or(where.toArray(new Predicate[0])));
        }

        cq.orderBy(cb.desc(root.get("createdAt")));

        final TypedQuery<UserEntity> q = em.createQuery(cq);
        q.setFirstResult((page.getNumber() - 1) * page.getLimit());
        q.setMaxResults(page.getLimit());

        List<User> users = new ArrayList<>();

        for (UserEntity userEntity : q.getResultList()) {
            users.add(userEntityMapper.toModel(userEntity));
        }

        return users;
    }

    @Override
    public List<User> findByNameName(String name)
    {
        List<User> users = new ArrayList<>();
        for (UserEntity r: userRepo.findByNameName(name))
        {
            users.add(userEntityMapper.toModel(r));
        }

        return users;
    }

    @Override
    public List<User> findByNameNameContains(String name)
    {
        List<User> users = new ArrayList<>();
        for (UserEntity r: userRepo.findByNameNameContains(name))
        {
            users.add(userEntityMapper.toModel(r));
        }

        return users;
    }
    @Override
    public void delete(User user)
    {

    }
}