package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.relational;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.OutboxEvent;
import pt.psoft.g1.psoftg1.usermanagement.model.OutboxEnum;

public interface SpringDataOutboxRepository extends CrudRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatus(OutboxEnum status);
}