package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.shared.model.mongodb.PhotoMongoDB;

import java.util.Optional;

public interface SpringDataPhotoRepositoryMongoDB extends MongoRepository<PhotoMongoDB, String> {
    @Query("{ 'id': ?0 }")
    Optional<PhotoMongoDB> findById(String photoId);

    @Query("{ 'photoFile': ?0 }")
    void deleteByPhotoFile(String photoFile);
}