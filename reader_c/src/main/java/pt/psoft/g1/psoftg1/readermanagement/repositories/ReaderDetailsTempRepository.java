package pt.psoft.g1.psoftg1.readermanagement.repositories;

import java.util.Optional;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;

public interface ReaderDetailsTempRepository
{
    ReaderDetails save(ReaderDetails readerDetails);
    void delete(String readerId);
    Optional<ReaderDetails> findByReaderId(String readerId);
}