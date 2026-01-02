package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.relational;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserEntityMapper;
import pt.psoft.g1.psoftg1.usermanagement.model.Librarian;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.LibrarianEntity;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserEntity;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;

import org.springframework.stereotype.Repository;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
@Component
public class UserRepositoryImpl implements UserRepository
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
        // TODO> Reader esta noutro microservico
        // if (entity instanceof Reader) {
        //     ReaderEntity readerEntity = userEntityMapper.toEntity((Reader) entity);
        //     ReaderEntity savedEntity = userRepo.save(readerEntity);
        //     return (S) userEntityMapper.toModel(savedEntity);

        // } 
        if (entity instanceof Librarian) {
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
    public void delete(User user)
    {
        //TODO:
    }
    
}
