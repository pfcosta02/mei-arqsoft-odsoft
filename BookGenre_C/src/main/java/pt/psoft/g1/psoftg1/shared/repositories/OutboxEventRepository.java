package pt.psoft.g1.psoftg1.shared.repositories;

import pt.psoft.g1.psoftg1.shared.model.OutboxEnum;
import pt.psoft.g1.psoftg1.shared.model.relational.OutboxEvent;

import java.util.List;

public interface OutboxEventRepository 
{
    List<OutboxEvent> findByStatus(OutboxEnum status);
    void save(OutboxEvent event);
}
