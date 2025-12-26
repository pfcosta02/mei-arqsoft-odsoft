package pt.psoft.g1.psoftg1.readermanagement.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderBookCountDTO;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReaderDetailsRepository {
    Optional<ReaderDetails> findByReaderNumber(@Param("readerNumber") @NotNull String readerNumber);
    List<ReaderDetails> findByPhoneNumber(@Param("phoneNumber") @NotNull String phoneNumber);
    Optional<ReaderDetails> findByEmail(@Param("email") @NotNull String email);
    Optional<ReaderDetails> findByReaderId(@Param("readerId") @NotNull String readerId);
    int getCountFromCurrentYear();
    ReaderDetails save(ReaderDetails readerDetails);
    Iterable<ReaderDetails> findAll();
    List<ReaderDetails> findTopReaders(Pageable pageable);
    List<ReaderBookCountDTO> findTopByGenre(Pageable pageable, String genre, LocalDate startDate, LocalDate endDate);
    void delete(ReaderDetails readerDetails);
    void delete(String readerDetailsId);
    List<ReaderDetails> searchReaderDetails(pt.psoft.g1.psoftg1.shared.services.Page page, SearchReadersQuery query);
}
