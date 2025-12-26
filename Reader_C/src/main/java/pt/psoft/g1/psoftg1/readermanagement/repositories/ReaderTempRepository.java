package pt.psoft.g1.psoftg1.readermanagement.repositories;

import java.util.Optional;

import pt.psoft.g1.psoftg1.readermanagement.model.Reader;

public interface ReaderTempRepository
{
    Reader save(Reader reader);
    void delete(String readerId);
    Optional<Reader> findByReaderId(String readerId);
    Optional<Reader> findByEmail(String email);
}