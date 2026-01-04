package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.relational;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.LendingEntity;

import java.util.*;

public interface SpringDataLendingRepository extends CrudRepository<LendingEntity, Long>
{

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE LendingEntity l
           SET l.returnedDate = :returnedDate,
               l.commentary   = :commentary,
               l.rating       = :rating,
               l.version      = :expectedVersion + 1
         WHERE l.lendingNumber.lendingNumber = :lendingNumber
           AND l.version = :expectedVersion
    """)
    int markReturned(
            @Param("lendingNumber") String lendingNumber,
            @Param("returnedDate") java.time.LocalDate returnedDate,
            @Param("commentary") String commentary,
            @Param("rating") Integer rating,
            @Param("expectedVersion") long expectedVersion    );


    @Query("SELECT l " +
            "FROM LendingEntity l " +
            "WHERE l.lendingNumber.lendingNumber = :lendingNumber")
    Optional<LendingEntity> findByLendingNumber(String lendingNumber);

    //http://www.h2database.com/html/commands.html

    @Query("SELECT l " +
            "FROM LendingEntity l " +
            "JOIN BookEntity b ON l.book.pk = b.pk " +
            "JOIN ReaderDetailsEntity r ON l.readerDetails.pk = r.pk " +
            "WHERE b.isbn.isbn = :isbn " +
            "AND r.readerNumber.readerNumber = :readerNumber ")
    List<LendingEntity> listByReaderNumberAndIsbn(String readerNumber, String isbn);

    @Query("SELECT COUNT (l) " +
            "FROM LendingEntity l " +
            "WHERE YEAR(l.startDate) = YEAR(CURRENT_DATE)")
    int getCountFromCurrentYear();

    @Query("SELECT l " +
            "FROM LendingEntity l " +
            "JOIN ReaderDetailsEntity r ON l.readerDetails.pk = r.pk " +
            "WHERE r.readerNumber.readerNumber = :readerNumber " +
            "AND l.returnedDate IS NULL")
    List<LendingEntity> listOutstandingByReaderNumber(@Param("readerNumber") String readerNumber);

    @Query(value =
            "SELECT AVG(DATEDIFF(day, l.start_date, l.returned_date)) " +
                    "FROM Lending l"
            , nativeQuery = true)
    Double getAverageDuration();

    @Query(value =
            "SELECT AVG(DATEDIFF(day, l.start_date, l.returned_date)) " +
                    "FROM Lending l " +
                    "JOIN BOOK b ON l.BOOK_PK = b.PK " +
                    "WHERE b.ISBN = :isbn"
            , nativeQuery = true)
    Double getAvgLendingDurationByIsbn(@Param("isbn") String isbn);
}