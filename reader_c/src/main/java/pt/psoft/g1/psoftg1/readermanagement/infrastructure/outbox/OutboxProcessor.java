package pt.psoft.g1.psoftg1.readermanagement.infrastructure.outbox;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.shared.model.ReaderEvents;

import pt.psoft.g1.psoftg1.readermanagement.model.OutboxEnum;
import pt.psoft.g1.psoftg1.readermanagement.model.relational.OutboxEvent;
import pt.psoft.g1.psoftg1.readermanagement.repositories.OutboxEventRepository;
import pt.psoft.g1.psoftg1.readermanagement.publishers.ReaderEventsPublisher;

@Service
public class OutboxProcessor {

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @Autowired
    private ReaderEventsPublisher publisher; // sua interface existente

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
                    case ReaderEvents.READER_CREATED:
                        publisher.publishReaderCreatedEvent(event.getPayload());
                        break;
                    case ReaderEvents.READER_UPDATED:
                        publisher.publishReaderUpdatedEvent(event.getPayload());
                        break;
                    case ReaderEvents.READER_DELETED:
                        publisher.publishReaderDeletedEvent(event.getPayload());
                        break;
                    case ReaderEvents.TEMP_READER_CREATED:
                        publisher.publishReaderTempCreatedEvent(event.getPayload());
                        break;
                    case ReaderEvents.TEMP_READER_PERSISTED:
                        publisher.publishReaderPersistedEvent(event.getPayload());
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