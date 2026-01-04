
package pt.psoft.g1.psoftg1.CDC.consumer;

import au.com.dius.pact.core.model.*;
import au.com.dius.pact.core.model.messaging.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import pt.psoft.g1.psoftg1.configs.JacksonTestConfig; // <-- importa a config Jackson
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingEventRabbitmqReceiver;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.shared.dtos.LendingEventAMQP;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@Import(JacksonTestConfig.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = { LendingEventRabbitmqReceiver.class }
)
public class LendingsCDCConsumerIT {

    @MockBean
    LendingService lendingService;

    @Autowired
    LendingEventRabbitmqReceiver lendingReceiver;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        reset(lendingService);
    }

    @Test
    void testLendingReturnedEventProcessing() throws Exception {
        File pactFile = new File("target/pacts/lendings_event-consumer-lendings_event-producer.json");

        if (!pactFile.exists()) {
            log.warn("⚠️ Pact file not found. Run consumer CDC first para gerar: mvn -Dtest=LendingsCDCDefinitionTest test");
            return;
        }

        PactReader pactReader = DefaultPactReader.INSTANCE;
        Pact pact = pactReader.loadPact(pactFile);

        List<Message> messagesFromPact = pact.asMessagePact().get().getMessages();
        log.info("[CDC CONSUMER] 📦 Found {} messages in Pact", messagesFromPact.size());

        for (Message messageFromPact : messagesFromPact) {
            processMessageForLendingReturned(messageFromPact);
        }
    }

    private void processMessageForLendingReturned(Message messageFromPact) throws Exception {
        String jsonPayload = messageFromPact.contentsAsString();
        log.debug("[CDC CONSUMER] 📨 Processing message: {}", jsonPayload);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");

        org.springframework.amqp.core.Message rabbitMessage =
                new org.springframework.amqp.core.Message(
                        jsonPayload.getBytes(StandardCharsets.UTF_8),
                        messageProperties
                );

        assertDoesNotThrow(() -> lendingReceiver.receiveLendingReturned(rabbitMessage));

        LendingEventAMQP event = objectMapper.readValue(jsonPayload, LendingEventAMQP.class);
        assertNotNull(event.lendingNumber);
        assertNotNull(event.bookIsbn);
        assertNotNull(event.readerNumber);
        assertNotNull(event.startDate);
        assertNotNull(event.returnedDate);

        if (messageFromPact.getDescription().contains("commentary and rating")) {
            assertNotNull(event.commentary);
            assertNotNull(event.rating);
            assertTrue(event.rating >= 0 && event.rating <= 10);
            log.info("✅ [CDC CONSUMER] Validado: commentary='{}', rating={}", event.commentary, event.rating);
        } else if (messageFromPact.getDescription().contains("without commentary")) {
            assertNull(event.commentary);
            assertNotNull(event.rating);
            log.info("✅ [CDC CONSUMER] Validado: sem commentary, rating={}", event.rating);
        }

        verify(lendingService, times(1)).updateFromEvent(any(LendingEventAMQP.class));
        log.info("✅ [CDC CONSUMER] Mensagem RETURNED processada: {}", event.lendingNumber);
    }

    // Os teus outros testes de CREATED/UPDATED/DELETED podem ficar como estão.
}
