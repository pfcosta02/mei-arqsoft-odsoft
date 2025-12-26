package pt.psoft.g1.psoftg1.authormanagement.services;

import pt.psoft.g1.psoftg1.authormanagement.api.AuthorLendingView;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQP;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    Iterable<Author> findAll();

    Optional<Author> findByAuthorNumber(Long authorNumber);

    List<Author> findByName(String name);

    Author create(CreateAuthorRequest resource); // REST request

    Author create(AuthorViewAMQP authorViewAMQP); // AMQP request

    Author partialUpdate(Long authorNumber, UpdateAuthorRequest resource, long desiredVersion);

    Author partialUpdate(AuthorViewAMQP authorViewAMQP);

    Optional<Author> removeAuthorPhoto(Long authorNumber, long desiredVersion);
}
