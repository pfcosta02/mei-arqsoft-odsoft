package pt.psoft.g1.psoftg1.shared.infrastructure.repositories.impl.relational;

import org.springframework.data.repository.CrudRepository;
import pt.psoft.g1.psoftg1.shared.model.OutboxEnum;
import pt.psoft.g1.psoftg1.shared.model.relational.OutboxEvent;

import java.util.List;

public interface SpringDataOutboxRepository extends CrudRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatus(OutboxEnum status);
}
