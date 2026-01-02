package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.impl.relational;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import pt.psoft.g1.psoftg1.lendingmanagement.model.relational.FineEntity;

import java.util.Optional;

public interface SpringDataFineRepository extends CrudRepository<FineEntity, Long>
{
    @Query("SELECT f " +
            "FROM FineEntity f " +
            "JOIN LendingEntity l ON f.lending.id = l.id " +
            "WHERE l.lendingNumber.lendingNumber = :lendingNumber")
    Optional<FineEntity> findByLendingNumber(String lendingNumber);

    @Query("SELECT f FROM FineEntity f")
    Iterable<FineEntity> findAll();
}