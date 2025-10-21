package pt.psoft.g1.psoftg1.authormanagement.repositories;

import org.springframework.data.domain.Pageable;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;

import java.util.List;
import java.util.Optional;


public interface AuthorRepository {

    Optional<Author> findByAuthorNumber(String authorNumber);
    List<Author> searchByNameNameStartsWith(String name);
    List<Author> searchByNameName(String name);
    Author save(Author author);
    Iterable<Author> findAll();
    List<AuthorLendingView> findTopAuthorByLendings (Pageable pageableRules);
    void delete(Author author);
    List<Author> findCoAuthorsByAuthorNumber(String authorNumber);

}