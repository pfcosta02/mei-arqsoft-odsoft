package pt.psoft.g1.psoftg1.usermanagement.infrastructure.repositories.impl.relational;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import pt.psoft.g1.psoftg1.usermanagement.model.OutboxEnum;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.OutboxEvent;
import pt.psoft.g1.psoftg1.usermanagement.repositories.OutboxEventRepository;

import java.util.List;

@Profile("jpa")
@Primary
@Repository
@RequiredArgsConstructor
@Component
public class OutboxRepositoryRelationalImpl implements OutboxEventRepository {
    private final SpringDataOutboxRepository outboxRepo;

    @Override
    public List<OutboxEvent> findByStatus(OutboxEnum status)
    {
        return outboxRepo.findByStatus(status);
    }

    @Override
    public void save(OutboxEvent event)
    {
        outboxRepo.save(event);
    }
}
