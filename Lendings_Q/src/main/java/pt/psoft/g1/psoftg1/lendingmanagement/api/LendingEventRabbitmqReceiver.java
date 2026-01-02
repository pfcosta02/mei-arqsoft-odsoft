package pt.psoft.g1.psoftg1.lendingmanagement.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.shared.dtos.LendingEventAMQP;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class LendingEventRabbitmqReceiver {

    private final LendingService service;
    private final ObjectMapper objectMapper;

    // ✅ CORRIGIDO: Construtor com 2 parâmetros
    public LendingEventRabbitmqReceiver(LendingService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "#{autoDeleteQueue_Lending_Created.name}")
    public void receiveLendingCreated(Message message) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            LendingEventAMQP event = objectMapper.readValue(json, LendingEventAMQP.class);
            service.createFromEvent(event);
            log.info("[Query] Received lending created: {}", event.getId());
        } catch (Exception e) {
            log.error("[Query] Error receiving lending created: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "#{autoDeleteQueue_Lending_Updated.name}")
    public void receiveLendingUpdated(Message message) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            LendingEventAMQP event = objectMapper.readValue(json, LendingEventAMQP.class);
            service.updateFromEvent(event);
            log.info("[Query] Received lending updated: {}", event.getId());
        } catch (Exception e) {
            log.error("[Query] Error receiving lending updated: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "#{autoDeleteQueue_Lending_Deleted.name}")
    public void receiveLendingDeleted(Message message) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            LendingEventAMQP event = objectMapper.readValue(json, LendingEventAMQP.class);
            service.deleteFromEvent(event);
            log.info("[Query] Received lending deleted: {}", event.getId());
        } catch (Exception e) {
            log.error("[Query] Error receiving lending deleted: {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "#{autoDeleteQueue_Lending_Returned.name}")
    public void receiveLendingReturned(Message message) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            LendingEventAMQP event = objectMapper.readValue(json, LendingEventAMQP.class);
            service.updateFromEvent(event);  // Atualizar é suficiente para return
            log.info("[Query] Received lending returned: {}", event.getId());
        } catch (Exception e) {
            log.error("[Query] Error receiving lending returned: {}", e.getMessage());
        }
    }
}