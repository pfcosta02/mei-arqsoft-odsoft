package pt.psoft.g1.psoftg1.usermanagement.infrastructure.outbox;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.shared.model.AuthNUsersEvents;

import pt.psoft.g1.psoftg1.usermanagement.infrastructure.publishers.impl.AuthNUsersEventsRabbitmqPublisher;
import pt.psoft.g1.psoftg1.usermanagement.model.OutboxEnum;
import pt.psoft.g1.psoftg1.usermanagement.model.relational.OutboxEvent;
import pt.psoft.g1.psoftg1.usermanagement.repositories.OutboxEventRepository;

@Service
public class OutboxProcessor {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private AuthNUsersEventsRabbitmqPublisher publisher;

    @Scheduled(fixedRate = 30000)
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByStatus(OutboxEnum.NEW);

        for (OutboxEvent event : events)
        {
            try
            {
                switch (event.getEventType())
                {
                    case AuthNUsersEvents.USER_CREATED:
                        publisher.publishUserCreatedEvent(event.getPayload());
                        break;
                    case AuthNUsersEvents.USER_UPDATED:
                        publisher.publishUserUpdatedEvent(event.getPayload());
                        break;
                    case AuthNUsersEvents.USER_DELETED:
                        publisher.publishUserDeletedEvent(event.getPayload());
                        break;
                    case AuthNUsersEvents.TEMP_USER_CREATED:
                        publisher.publishUserTempCreatedEvent(event.getPayload());
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
                }

                event.setStatus(OutboxEnum.SENT);
                outboxEventRepository.save(event);
            }
            catch (Exception e)
            {
                event.setStatus(OutboxEnum.ERROR);
                outboxEventRepository.save(event);
                e.printStackTrace();
            }
        }
    }
}