package pt.psoft.g1.psoftg1.readermanagement.repositories;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.query.Param;
import pt.psoft.g1.psoftg1.readermanagement.services.SearchReadersQuery;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import java.util.List;
import java.util.Optional;

public interface ReaderDetailsRepository
{
    Optional<ReaderDetails> findByReaderNumber(@Param("readerNumber") @NotNull String readerNumber);
    Optional<ReaderDetails> findByEmail(@Param("email") @NotNull String email);
    Optional<ReaderDetails> findByUserId(@Param("readerId") @NotNull String readerId);
    int getCountFromCurrentYear();
    ReaderDetails save(ReaderDetails readerDetails);
    void delete(ReaderDetails readerDetails);
    List<ReaderDetails> searchReaderDetails(pt.psoft.g1.psoftg1.shared.services.Page page, SearchReadersQuery query);

    List<ReaderDetails> findAll();
}