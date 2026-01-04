package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.publishers.LendingEventPublisher;
import pt.psoft.g1.psoftg1.shared.dtos.LendingEventAMQP;
import pt.psoft.g1.psoftg1.shared.model.LendingEvents;

@Service
public class LendingEventsRabbitmqPublisherImpl implements LendingEventPublisher {

    private final RabbitTemplate template;
    private final DirectExchange exchange;

    public LendingEventsRabbitmqPublisherImpl(
            RabbitTemplate template,
            @Qualifier("lendingsExchange") DirectExchange exchange) {
        this.template = template;
        this.exchange = exchange;
    }

    @Override
    public void publishLendingCreated(Lending lending) {
        publishEvent(lending, LendingEvents.LENDING_CREATED);
    }

    @Override
    public void publishLendingUpdated(Lending lending) {
        publishEvent(lending, LendingEvents.LENDING_UPDATED);
    }

    @Override
    public void publishLendingDeleted(Lending lending) {
        publishEvent(lending, LendingEvents.LENDING_DELETED);
    }

    private void publishEvent(Lending lending, String routingKey) {
        try {
            LendingEventAMQP event = LendingEventAMQP.from(lending);
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(event);

            template.convertAndSend(exchange.getName(), routingKey, json);
            System.out.println("[Command] Published: " + routingKey);
        } catch (Exception e) {
            System.err.println("[Command] Error publishing event: " + e.getMessage());
        }
    }
}