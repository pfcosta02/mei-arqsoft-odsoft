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

    public LendingEventRabbitmqReceiver(LendingService service, ObjectMapper objectMapper) {
        this.service = service;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "#{autoDeleteQueue_Lending_Created.name}")
    public void receiveLendingCreated(Message message) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            log.debug("[Query] Received raw JSON: {}", json);

            // Remove quotes extra se houver (double encoding)
            if (json.startsWith("\"") && json.endsWith("\"")) {
                json = json.substring(1, json.length() - 1);
                // Unescape escaped quotes
                json = json.replace("\\\"", "\"");
            }

            LendingEventAMQP event = objectMapper.readValue(json, LendingEventAMQP.class);
            service.createFromEvent(event);
            log.info("[Query] Synced lending created: {}", event.lendingNumber);
        } catch (Exception e) {
            log.error("[Query] Error receiving lending created: {}", e.getMessage(), e);
            // NÃO lança exceção - permite que a mensagem seja descartada
        }
    }

    @RabbitListener(queues = "#{autoDeleteQueue_Lending_Updated.name}")
    public void receiveLendingUpdated(Message message) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            log.debug("[Query] Received raw JSON: {}", json);

            if (json.startsWith("\"") && json.endsWith("\"")) {
                json = json.substring(1, json.length() - 1);
                json = json.replace("\\\"", "\"");
            }

            LendingEventAMQP event = objectMapper.readValue(json, LendingEventAMQP.class);
            service.updateFromEvent(event);
            log.info("[Query] Synced lending updated: {}", event.lendingNumber);
        } catch (Exception e) {
            log.error("[Query] Error receiving lending updated: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "#{autoDeleteQueue_Lending_Deleted.name}")
    public void receiveLendingDeleted(Message message) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            log.debug("[Query] Received raw JSON: {}", json);

            if (json.startsWith("\"") && json.endsWith("\"")) {
                json = json.substring(1, json.length() - 1);
                json = json.replace("\\\"", "\"");
            }

            LendingEventAMQP event = objectMapper.readValue(json, LendingEventAMQP.class);
            service.deleteFromEvent(event);
            log.info("[Query] Synced lending deleted: {}", event.lendingNumber);
        } catch (Exception e) {
            log.error("[Query] Error receiving lending deleted: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "#{autoDeleteQueue_Lending_Returned.name}")
    public void receiveLendingReturned(Message message) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            log.debug("[Query] Received raw JSON: {}", json);

            if (json.startsWith("\"") && json.endsWith("\"")) {
                json = json.substring(1, json.length() - 1);
                json = json.replace("\\\"", "\"");
            }

            LendingEventAMQP event = objectMapper.readValue(json, LendingEventAMQP.class);
            service.updateFromEvent(event);
            log.info("[Query] Synced lending returned: {}", event.lendingNumber);
        } catch (Exception e) {
            log.error("[Query] Error receiving lending returned: {}", e.getMessage(), e);
        }
    }
}