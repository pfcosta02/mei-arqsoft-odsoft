package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.usermanagement.model.mongodb.UserMongoDB;

import java.util.List;
import java.util.Optional;

/**
 * Based on https://github.com/Yoh0xFF/java-spring-security-example
 *
 */
@CacheConfig(cacheNames = "users")
public interface SpringDataUserRepositoryMongoDB extends MongoRepository<UserMongoDB, String> {

    @CacheEvict(allEntries = true)
    <S extends UserMongoDB> List<S> saveAll(Iterable<S> entities);


    @Caching(evict = {
            @CacheEvict(key = "#p0.id", condition = "#p0.id != null"),
            @CacheEvict(key = "#p0.username", condition = "#p0.username != null")
    })
    <S extends UserMongoDB> S save(S entity);


    @Cacheable
    Optional<UserMongoDB> findById(String userId);


    @Cacheable
    default UserMongoDB getById(final String id) {
        final Optional<UserMongoDB> maybeUser = findById(id);
        return maybeUser.filter(UserMongoDB::isEnabled)
                .orElseThrow(() -> new NotFoundException(UserMongoDB.class, id));
    }


    @Cacheable
    @Query("{ 'username': ?0 }")
    Optional<UserMongoDB> findByUsername(String username);


    @Cacheable
    @Query("{ 'name.name': ?0 }")
    List<UserMongoDB> findByNameName(String name);

    @Cacheable
    @Query("{ 'name.name': { $regex: ?0, $options: 'i' } }")
    List<UserMongoDB> findByNameNameContains(String namePart);

}