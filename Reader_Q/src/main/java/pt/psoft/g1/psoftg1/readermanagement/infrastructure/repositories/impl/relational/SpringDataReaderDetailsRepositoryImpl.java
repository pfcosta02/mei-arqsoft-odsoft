package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SpringDataReaderDetailsRepositoryImpl extends CrudRepository<ReaderDetailsEntity, String>
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
            "WHERE r.reader.email = :email")
    Optional<ReaderDetailsEntity> findByEmail(@Param("email") @NotNull String email);

    @Query("SELECT r " +
            "FROM ReaderDetailsEntity r " +
            "JOIN ReaderEntity u ON r.reader.readerId = u.readerId " +
            "WHERE u.readerId = :readerId")
    Optional<ReaderDetailsEntity> findByReaderId(@Param("readerId") @NotNull String readerId);


    @Query("SELECT COUNT(rd) FROM ReaderDetailsEntity rd " +
            "WHERE rd.readerNumber.readerNumber LIKE CONCAT(FUNCTION('YEAR', CURRENT_DATE), '/%')")
    int getCountFromCurrentYear();

    @Query("SELECT rd " +
            "FROM ReaderDetailsEntity rd " +
            "JOIN LendingEntity l ON l.readerDetails.id = rd.id " +
            "GROUP BY rd " +
            "ORDER BY COUNT(l) DESC")
    List<ReaderDetailsEntity> findTopReaders(Pageable pageable);

//     TODO> Rever esta query
//     @Query("SELECT NEW pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO(rd, count(l)) " +
//             "FROM ReaderDetailsEntity rd " +
//             "JOIN LendingEntity l ON l.readerDetails.id = rd.id " +
//             "JOIN BookEntity b ON b.id = l.book.id " +
//             "JOIN GenreEntity g ON g.id = b.genre.id " +
//             "WHERE g.genre = :genre " +
//             "AND l.startDate >= :startDate " +
//             "AND l.startDate <= :endDate " +
//             "GROUP BY rd.id " +
//             "ORDER BY COUNT(l.id) DESC")
//     List<ReaderBookCountDTO> findTopByGenre(Pageable pageable, String genre, LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM ReaderDetailsEntity r")
    Iterable<ReaderDetailsEntity> findAll();
}

