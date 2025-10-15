package pt.psoft.g1.psoftg1.authormanagement.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository {

    Optional<Author> findByAuthorNumber(String authorNumber);
    List<Author> searchByNameNameStartsWith(String name);
    List<Author> searchByNameName(String name);
    Author save(Author author);
    Iterable<Author> findAll();
    Page<AuthorLendingView> findTopAuthorByLendings (Pageable pageableRules);
    void delete(Author author);
    List<Author> findCoAuthorsByAuthorNumber(String authorNumber);

}