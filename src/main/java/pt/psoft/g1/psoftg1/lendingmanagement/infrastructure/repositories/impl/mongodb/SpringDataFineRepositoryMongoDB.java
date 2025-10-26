package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.FineMongoDB;

import java.util.List;
import java.util.Optional;

public interface SpringDataFineRepositoryMongoDB extends MongoRepository<FineMongoDB, String> {
    @Query("{ 'lendingNumber' : ?0 }")
    Optional<FineMongoDB> findByLendingNumber(String lendingNumber);

    @Query("{}")
    List<FineMongoDB> findAll();
}
