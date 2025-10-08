package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import pt.psoft.g1.psoftg1.lendingmanagement.model.mongodb.LendingMongoDB;

import java.util.List;
import java.util.Optional;

public interface SpringDataLendingRepositoryMongoDB extends MongoRepository<LendingMongoDB, Long> {
    @Query("{ 'lendingNumber': ?0 }")
    Optional<LendingMongoDB> findByLendingNumber(String lendingNumber);

    @Query("{ 'book.isbn': ?1, 'reader.readerNumber': ?0 }")
    List<LendingMongoDB> listByReaderNumberAndIsbn(String readerNumber, String isbn);

    int getCountFromCurrentYear();

    @Query("{ 'reader.readerNumber': ?0, 'returnedDate': null }")
    List<LendingMongoDB> listOutstandingByReaderNumber(String readerNumber);

    Double getAverageDuration();

    @Query("{ 'isbn' : ?0 }")
    Double getAvgLendingDurationByIsbn(String isbn);
}
