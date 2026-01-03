package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import jakarta.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pt.psoft.g1.psoftg1.readermanagement.model.relational.ReaderDetailsEntity;

import org.springframework.data.domain.Pageable;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SpringDataReaderRepositoryImpl extends CrudRepository<ReaderDetailsEntity, Long>
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
            "JOIN UserEntity u ON r.reader.id = u.id " +
            "WHERE u.id = :userId")
    Optional<ReaderDetailsEntity> findByUserId(@Param("userId") @NotNull Long userId);

    @Query("SELECT COUNT (rd) " +
            "FROM ReaderDetailsEntity rd " +
            "JOIN UserEntity u ON rd.reader.id = u.id " +
            "WHERE YEAR(u.createdAt) = YEAR(CURRENT_DATE)")
    int getCountFromCurrentYear();

    @Query("SELECT rd " +
            "FROM ReaderDetailsEntity rd " +
            "JOIN LendingEntity l ON l.readerDetails.pk = rd.pk " +
            "GROUP BY rd " +
            "ORDER BY COUNT(l) DESC")
    Page<ReaderDetailsEntity> findTopReaders(Pageable pageable);

    Iterable<ReaderDetailsEntity> findAll();
}

