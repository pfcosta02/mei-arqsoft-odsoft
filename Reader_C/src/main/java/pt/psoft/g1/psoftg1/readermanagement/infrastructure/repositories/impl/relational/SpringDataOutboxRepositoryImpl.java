package pt.psoft.g1.psoftg1.readermanagement.infrastructure.repositories.impl.relational;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.OutboxEvent;
import pt.psoft.g1.psoftg1.readermanagement.model.OutboxEnum;

public interface SpringDataOutboxRepositoryImpl extends CrudRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatus(OutboxEnum status);
}