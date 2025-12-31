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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.dto.ReaderDetailsDTO;

import pt.psoft.g1.psoftg1.configs.TestConfig;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import pt.psoft.g1.psoftg1.readermanagement.api.ReaderRabbitmqController;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(TestConfig.class)
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE
        ,classes = {ReaderRabbitmqController.class, ReaderService.class}
)
@PactConsumerTest
@PactTestFor(providerName = "readers_event-producer", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V4)
public class ReadersCDCDefinitionTest
{

    @MockBean
    ReaderService readerService;

    @Autowired
    ReaderRabbitmqController listener;

    /* Criar o Contrato */
    @Pact(consumer = "readers_created-consumer")
    V4Pact createReaderCreatedPact(MessagePactBuilder builder)
    {
        PactDslJsonBody body = new PactDslJsonBody()
                .stringType("id", "1")
                .integerType("version", 1)
                .booleanType("gdprConsent", true)
                .booleanType("marketingConsent", true)
                .booleanType("thirdPartySharingConsent", true)
                .stringType("photo", "photo.jpg");

        // reader (objeto aninhado)
        PactDslJsonBody reader = body.object("reader");
        reader
                .stringType("readerId", "1")
                .stringType("userId", "1");

        // reader.name (objeto aninhado)
        PactDslJsonBody readerName = reader.object("name");
        readerName
                .stringType("name", "Pedro Soares")
                .closeObject(); // fecha reader.name

        reader
                .stringType("email", "pedro@email.com")
                .closeObject(); // fecha reader

        // Value objects - readerNumber and phoneNumber are simple strings
        body.stringMatcher("readerNumber", "\\d{4}/\\d+", "2025/1");
        body.stringMatcher("phoneNumber", "[29]\\d{8}", "919191919");

        // birthDate is a nested object (BirthDateDTO)
        PactDslJsonBody birthDateObj = body.object("birthDate");
        birthDateObj
                .stringType("birthDate", "2000-01-01")
                .closeObject();

        // interestList (array de strings)
        body.array("interestList")
                .closeArray();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("Content-Type", "application/json");

        return builder.expectsToReceive("a permanent reader with his details created event").withMetadata(metadata).withContent(body).toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createReaderCreatedPact")
    void testReaderCreated(List<V4Interaction.AsynchronousMessage> messages) throws Exception
    {
        // Convert the Pact message to a String (JSON payload)
        String jsonReceived = messages.get(0).contentsAsString();

        // Create a Spring AMQP Message with the JSON payload and optional headers
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        Message message = new Message(jsonReceived.getBytes(StandardCharsets.UTF_8), messageProperties);

        // Simulate receiving the message in the listener
        assertDoesNotThrow(() -> {
            listener.consumeMessage_ReaderCreated(message);
        });

        // Verify interactions with the mocked service
        verify(readerService, times(1)).createEvent(any(ReaderDetailsDTO.class));
    }
}