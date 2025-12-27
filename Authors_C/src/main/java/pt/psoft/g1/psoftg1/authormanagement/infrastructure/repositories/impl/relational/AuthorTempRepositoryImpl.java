package pt.psoft.g1.psoftg1.authormanagement.infrastructure.repositories.impl.relational;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorTempEntity;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorTempRepository;

import java.util.Optional;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
public class AuthorTempRepositoryImpl implements AuthorTempRepository {

    private final SpringDataAuthorTempRepository authorTempRepository;

    @Override
    public AuthorTempEntity save(AuthorTempEntity authorTempEntity) {
        return authorTempRepository.save(authorTempEntity);
    }

    @Override
    public Optional<AuthorTempEntity> findByIsbn(String isbn) {
        return authorTempRepository.findByIsbn(isbn);
    }
}
