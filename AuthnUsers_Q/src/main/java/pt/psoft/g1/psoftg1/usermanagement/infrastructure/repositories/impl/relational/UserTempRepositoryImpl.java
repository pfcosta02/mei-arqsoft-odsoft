package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.relational;

import pt.psoft.g1.psoftg1.usermanagement.repositories.UserTempRepository;
import pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mappers.UserEntityMapper;
import pt.psoft.g1.psoftg1.usermanagement.model.User;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserTempEntity;

import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
@Component
public class UserTempRepositoryImpl implements UserTempRepository
{
    private final SpringDataUserTempRepository userTempRepo;
    private final UserEntityMapper userEntityMapper;

    @Override
    public User save(User user)
    {
        UserTempEntity entity = userEntityMapper.toTempEntity(user);
        UserTempEntity savedEntity = userTempRepo.save(entity);

        return userEntityMapper.toModelFromTemp(savedEntity);
    }

    @Override
    public Optional<User> findByUserId(String userId)
    {
        Optional<UserTempEntity> entity = userTempRepo.findByUserId(userId);
        if (entity.isPresent())
        {
            return Optional.of(userEntityMapper.toModelFromTemp(entity.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username)
    {
        Optional<UserTempEntity> entity = userTempRepo.findByUsername(username);
        if (entity.isPresent())
        {
            return Optional.of(userEntityMapper.toModelFromTemp(entity.get()));
        }
        else
        {
            return Optional.empty();
        }
    }

    @Override
    public void delete(String userId)
    {
        userTempRepo.deleteById(userId);
    }
}
