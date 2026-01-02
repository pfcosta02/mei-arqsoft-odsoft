package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingOutbox;
import java.util.List;

@Repository
public interface LendingOutboxRepository extends JpaRepository<LendingOutbox, Long> {
    List<LendingOutbox> findByPublishedFalse();
}
