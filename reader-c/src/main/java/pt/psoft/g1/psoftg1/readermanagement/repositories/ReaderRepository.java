package pt.psoft.g1.psoftg1.readermanagement.repositories;

import pt.psoft.g1.psoftg1.readermanagement.model.Reader;
import java.util.Optional;

public interface ReaderRepository {
    Reader save(Reader reader);
    Optional<Reader> findByEmail(String email);
}