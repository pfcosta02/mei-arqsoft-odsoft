package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.shared.model.mongodb.ForbiddenNameMongoDB;

import java.util.List;
import java.util.Optional;

public interface SpringDataForbiddenNameRepositoryMongoDB extends MongoRepository<ForbiddenNameMongoDB, String> {

    @Query("{ 'forbiddenName': { $regex: ?0, $options: 'i' } }")
    List<ForbiddenNameMongoDB> findByForbiddenNameIsContained(String pat);

    @Query("{ 'forbiddenName': ?0 }")
    Optional<ForbiddenNameMongoDB> findByForbiddenName(String forbiddenName);

    @Query(value = "{ 'forbiddenName': ?0 }", delete = true)
    int deleteForbiddenName(String forbiddenName);

    @Query("{}")
    List<ForbiddenNameMongoDB> findAll();
}
