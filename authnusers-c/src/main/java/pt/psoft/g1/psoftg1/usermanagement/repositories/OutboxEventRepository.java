package pt.psoft.g1.psoftg1.usermanagement.repositories;

import pt.psoft.g1.psoftg1.usermanagement.model.OutboxEnum;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.OutboxEvent;

import java.util.List;

public interface OutboxEventRepository {
    List<OutboxEvent> findByStatus(OutboxEnum status);
    void save(OutboxEvent event);
}