package pt.psoft.g1.psoftg1.authormanagement.repositories;

import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorTempEntity;

import java.util.Optional;

public interface AuthorTempRepository {
    AuthorTempEntity save(AuthorTempEntity authorTempEntity);
    void delete(AuthorTempEntity authorTempEntity);
    Optional<AuthorTempEntity> findByAuthorNumber(String authorNumber);
}
