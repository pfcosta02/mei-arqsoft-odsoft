package pt.psoft.g1.psoftg1.authormanagement.infrastructure.outbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.authormanagement.publishers.AuthorEventsPublisher;
import pt.psoft.g1.psoftg1.shared.model.AuthorEvents;
import pt.psoft.g1.psoftg1.shared.model.BookEvents;
import pt.psoft.g1.psoftg1.shared.model.OutboxEnum;
import pt.psoft.g1.psoftg1.shared.model.relational.OutboxEvent;
import pt.psoft.g1.psoftg1.shared.repositories.OutboxEventRepository;

import java.util.List;

@Service
public class OutboxProcessor {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private AuthorEventsPublisher publisher;

    @Scheduled(fixedRate = 5000)
    public void processOutboxEvents() {
        List<OutboxEvent> events = outboxEventRepository.findByStatus(OutboxEnum.NEW);

        System.out.println("Processing " + events.size() + " outbox events.");
        for (OutboxEvent event : events)
        {
            try
            {
                switch (event.getEventType())
                {
                    case AuthorEvents.AUTHOR_CREATED:
                        publisher.sendAuthorCreated(event.getPayload());
                        break;
//                    case BookEvents.BOOK_UPDATED:
//                        publisher.publishBookUpdatedEvent(event.getPayload());
//                        break;
//                    case BookEvents.BOOK_DELETED:
//                        publisher.publishBookDeletedEvent(event.getPayload());
//                        break;
                    case AuthorEvents.TEMP_AUTHOR_CREATED:
                        publisher.sendAuthorTempCreated(event.getPayload());
                        break;
//                    case AuthorEvents.TEMP_AUTHOR_PERSISTED:
//                        publisher.publishReaderPersistedEvent(event.getPayload());
//                        break;
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
