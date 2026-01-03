package pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.publishers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.repositories.LendingOutboxRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.LendingOutbox;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPoller {

    private final LendingOutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;

    @Qualifier("lendingsExchange")
    private final DirectExchange lendingsExchange;

    @Scheduled(fixedDelay = 5000)  // Executa a cada 5 segundos
    @Transactional
    public void pollAndPublish() {
        try {
            // 1. Procura eventos não publicados
            List<LendingOutbox> unpublished = outboxRepository.findByPublishedFalse();

            if (unpublished.isEmpty()) {
                return;  // Nada para publicar
            }

            log.info("Found {} unpublished events", unpublished.size());

            // 2. Publica cada evento
            for (LendingOutbox event : unpublished) {
                try {
                    // Envia para RabbitMQ
                    rabbitTemplate.convertAndSend(
                            lendingsExchange.getName(),
                            event.getEventType(),
                            event.getPayload()
                    );

                    // 3. Marca como publicado
                    event.setPublished(true);
                    event.setPublishedAt(LocalDateTime.now());
                    outboxRepository.save(event);

                    log.info("Published event: {} (aggregate: {})",
                            event.getEventType(), event.getAggregateId());
                } catch (Exception e) {
                    log.error("Failed to publish event: {}", event.getId(), e);
                    // Deixa para próxima tentativa (importante!)
                }
            }
        } catch (Exception e) {
            log.error("Error in outbox poller: ", e);
        }
    }
}

