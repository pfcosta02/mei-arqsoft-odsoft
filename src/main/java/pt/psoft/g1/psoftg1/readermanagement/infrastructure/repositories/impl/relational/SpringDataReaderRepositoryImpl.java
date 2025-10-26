package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import org.springframework.data.domain.Pageable;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataReaderRepositoryImpl extends CrudRepository<ReaderDetailsEntity, String>
{
    @Query("SELECT r " +
            "FROM ReaderDetailsEntity r " +
            "WHERE r.readerNumber.readerNumber = :readerNumber")
    Optional<ReaderDetailsEntity> findByReaderNumber(@Param("readerNumber") @NotNull String readerNumber);

    @Query("SELECT r " +
            "FROM ReaderDetailsEntity r " +
            "WHERE r.phoneNumber.phoneNumber = :phoneNumber")
    List<ReaderDetailsEntity> findByPhoneNumber(@Param("phoneNumber") @NotNull String phoneNumber);

    @Query("SELECT r " +
            "FROM ReaderDetailsEntity r " +
            "WHERE r.reader.username = :username")
    Optional<ReaderDetailsEntity> findByUsername(@Param("username") @NotNull String username);

    @Query("SELECT r " +
            "FROM ReaderDetailsEntity r " +
            "JOIN UserEntity u ON r.reader.userId = u.userId " +
            "WHERE u.userId = :userId")
    Optional<ReaderDetailsEntity> findByUserId(@Param("userId") @NotNull String userId);

    @Query("SELECT COUNT (rd) " +
            "FROM ReaderDetailsEntity rd " +
            "JOIN UserEntity u ON rd.reader.userId = u.userId " +
            "WHERE YEAR(u.createdAt) = YEAR(CURRENT_DATE)")
    int getCountFromCurrentYear();

    @Query("SELECT rd " +
            "FROM ReaderDetailsEntity rd " +
            "JOIN LendingEntity l ON l.readerDetails.readerDetailsId = rd.readerDetailsId " +
            "GROUP BY rd " +
            "ORDER BY COUNT(l) DESC")
    List<ReaderDetailsEntity> findTopReaders(Pageable pageable);

    @Query("SELECT NEW pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO(rd, count(l)) " +
            "FROM ReaderDetailsEntity rd " +
            "JOIN LendingEntity l ON l.readerDetails.readerDetailsId = rd.readerDetailsId " +
            "JOIN BookEntity b ON b.bookId = l.book.bookId " +
            "JOIN GenreEntity g ON g.pk = b.genre.pk " +
            "WHERE g.genre = :genre " +
            "AND l.startDate >= :startDate " +
            "AND l.startDate <= :endDate " +
            "GROUP BY rd.readerDetailsId " +
            "ORDER BY COUNT(l.lendingId) DESC")
    List<ReaderBookCountDTO> findTopByGenre(Pageable pageable, String genre, LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM ReaderDetailsEntity r")
    Iterable<ReaderDetailsEntity> findAll();
}

