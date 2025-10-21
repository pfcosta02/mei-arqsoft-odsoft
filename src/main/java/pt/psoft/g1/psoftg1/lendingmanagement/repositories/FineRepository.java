package pt.psoft.g1.psoftg1.lendingmanagement.repositories;

import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;

import java.util.Optional;

@Repository
public interface FineRepository {

    Optional<Fine> findByLendingNumber(String lendingNumber);
    Iterable<Fine> findAll();

    Fine save(Fine fine);

}
