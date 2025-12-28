package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.relational;

import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import pt.psoft.g1.psoftg1.usermanagement.model.relational.UserTempEntity;

@CacheConfig(cacheNames = "usersTemp")
public interface SpringDataUserTempRepository extends CrudRepository<UserTempEntity, String>
{
    @Caching(evict = { @CacheEvict(key = "#p0.id", condition = "#p0.id != null"),
			@CacheEvict(key = "#p0.username", condition = "#p0.username != null") })
	UserTempEntity save(UserTempEntity entity);

    @Cacheable
    @Query("SELECT u FROM UserTempEntity u LEFT JOIN FETCH u.authorities WHERE u.id = ?1")
    Optional<UserTempEntity> findByUserId(String userId);

    @Cacheable
    @Query("SELECT u FROM UserTempEntity u LEFT JOIN FETCH u.authorities WHERE u.username = ?1")
    Optional<UserTempEntity> findByUsername(String username);
}
