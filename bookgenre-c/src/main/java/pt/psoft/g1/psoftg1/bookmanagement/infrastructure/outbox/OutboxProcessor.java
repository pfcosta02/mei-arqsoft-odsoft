package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.outbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.bookmanagement.publishers.BookEventsPublisher;
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
    private BookEventsPublisher publisher;

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
                    case BookEvents.BOOK_CREATED:
                        publisher.sendBookCreated(event.getPayload());
                        break;
                    case BookEvents.TEMP_BOOK_CREATED:
                        publisher.sendBookTempCreated(event.getPayload());
                        break;
                    case BookEvents.BOOK_FINALIZED:
                        publisher.sendBookFinalized(event.getPayload());
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
