package pt.psoft.g1.psoftg1.readermanagement.repositories;

import pt.psoft.g1.psoftg1.readermanagement.model.OutboxEnum;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.OutboxEvent;

import java.util.List;

public interface OutboxEventRepository
{
    List<OutboxEvent> findByStatus(OutboxEnum status);
    void save(OutboxEvent event);
}