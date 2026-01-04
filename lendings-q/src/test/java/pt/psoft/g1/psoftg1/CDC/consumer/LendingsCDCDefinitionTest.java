
package pt.psoft.g1.psoftg1.CDC.consumer;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTest;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import pt.psoft.g1.psoftg1.configs.JacksonTestConfig; // <-- importa a config Jackson dos testes
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingEventRabbitmqReceiver;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingService;
import pt.psoft.g1.psoftg1.shared.dtos.LendingEventAMQP;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@Import(JacksonTestConfig.class) // garante JavaTimeModule no ObjectMapper do contexto
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = { LendingEventRabbitmqReceiver.class }
)
@PactConsumerTest
@PactTestFor(
        providerName = "lendings_event-producer",
        providerType = ProviderType.ASYNCH,
        pactVersion = PactSpecVersion.V4
)
public class LendingsCDCDefinitionTest {

    @MockBean
    LendingService lendingService;

    @Autowired
    LendingEventRabbitmqReceiver lendingReceiver;

    @Pact(consumer = "lendings_event-consumer")
    V4Pact createLendingReturnedWithCommentaryPact(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody()
                .stringType("lendingNumber", "2025/1")
                .stringType("bookIsbn", "978-1234567890")
                .stringType("readerNumber", "R001")
                .stringType("startDate", "2025-01-01")
                .stringType("returnedDate", "2025-01-15")
                .stringType("commentary", "Excelente livro, muito recomendado!")
                .integerType("rating", 9)
                .numberType("version", 6L);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("Content-Type", "application/json");

        return builder
                .expectsToReceive("a lending returned with commentary and rating event")
                .withMetadata(metadata)
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "lendings_event-consumer")
    V4Pact createLendingReturnedWithoutCommentaryPact(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody()
                .stringType("lendingNumber", "2025/2")
                .stringType("bookIsbn", "978-0987654321")
                .stringType("readerNumber", "R002")
                .stringType("startDate", "2025-01-05")
                .stringType("returnedDate", "2025-01-20")
                .nullValue("commentary")
                .integerType("rating", 7)
                .numberType("version", 3L);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("Content-Type", "application/json");

        return builder
                .expectsToReceive("a lending returned without commentary event")
                .withMetadata(metadata)
                .withContent(body)
                .toPact();
    }

    @Pact(consumer = "lendings_event-consumer")
    V4Pact createLendingCreatedPact(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody()
                .stringType("lendingNumber", "2025/3")
                .stringType("bookIsbn", "978-1111111111")
                .stringType("readerNumber", "R003")
                .stringType("startDate", "2025-01-10")
                .nullValue("returnedDate")
                .nullValue("commentary")
                .nullValue("rating")
                .numberType("version", 1L);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("Content-Type", "application/json");

        return builder
                .expectsToReceive("a lending created event")
                .withMetadata(metadata)
                .withContent(body)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createLendingReturnedWithCommentaryPact")
    void testLendingReturnedWithCommentary(List<V4Interaction.AsynchronousMessage> messages) throws Exception {
        String jsonPayload = messages.get(0).contentsAsString();
        log.info("[PACT TEST] Received: {}", jsonPayload);

        MessageProperties mp = new MessageProperties();
        mp.setContentType("application/json");
        Message message = new Message(jsonPayload.getBytes(StandardCharsets.UTF_8), mp);

        assertDoesNotThrow(() -> lendingReceiver.receiveLendingReturned(message));
        verify(lendingService, times(1)).updateFromEvent(any(LendingEventAMQP.class));
        log.info("✅ [PACT TEST] Lending returned (com commentary) OK");
    }

    @Test
    @PactTestFor(pactMethod = "createLendingReturnedWithoutCommentaryPact")
    void testLendingReturnedWithoutCommentary(List<V4Interaction.AsynchronousMessage> messages) throws Exception {
        String jsonPayload = messages.get(0).contentsAsString();
        log.info("[PACT TEST] Received: {}", jsonPayload);

        MessageProperties mp = new MessageProperties();
        mp.setContentType("application/json");
        Message message = new Message(jsonPayload.getBytes(StandardCharsets.UTF_8), mp);

        assertDoesNotThrow(() -> lendingReceiver.receiveLendingReturned(message));
        verify(lendingService, times(1)).updateFromEvent(any(LendingEventAMQP.class));
        log.info("✅ [PACT TEST] Lending returned (sem commentary) OK");
    }

    @Test
    @PactTestFor(pactMethod = "createLendingCreatedPact")
    void testLendingCreated(List<V4Interaction.AsynchronousMessage> messages) throws Exception {
        String jsonPayload = messages.get(0).contentsAsString();
        log.info("[PACT TEST] Received: {}", jsonPayload);

        MessageProperties mp = new MessageProperties();
        mp.setContentType("application/json");
        Message message = new Message(jsonPayload.getBytes(StandardCharsets.UTF_8), mp);

        assertDoesNotThrow(() -> lendingReceiver.receiveLendingCreated(message));
        verify(lendingService, times(1)).createFromEvent(any(LendingEventAMQP.class));
        log.info("✅ [PACT TEST] Lending created OK");
    }
}
