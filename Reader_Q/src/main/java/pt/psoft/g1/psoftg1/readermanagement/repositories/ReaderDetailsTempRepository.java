package pt.psoft.g1.psoftg1.readermanagement.repositories;

import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import java.util.Optional;

public interface ReaderDetailsTempRepository {
    ReaderDetails save(ReaderDetails readerDetails);
    void delete(String readerId);
    Optional<ReaderDetails> findByReaderId(String readerId);
}
