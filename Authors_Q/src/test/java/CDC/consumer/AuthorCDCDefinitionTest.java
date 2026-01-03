package CDC.consumer;

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
import configs.TestConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorRabbitmqController;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQP;
import pt.psoft.g1.psoftg1.authormanagement.services.AuthorService;

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
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {AuthorRabbitmqController.class, AuthorService.class}
)
@PactConsumerTest
@PactTestFor(
        providerName = "author_event-producer",
        providerType = ProviderType.ASYNCH,
        pactVersion = PactSpecVersion.V4
)
public class AuthorCDCDefinitionTest {

    @MockBean
    AuthorService authorService;

    @Autowired
    AuthorRabbitmqController listener;

    /* ========= CONTRACT ========= */
    @Pact(consumer = "author_created-consumer")
    V4Pact authorCreatedPact(MessagePactBuilder builder) {

        PactDslJsonBody body = new PactDslJsonBody()
                .stringType("authorNumber", "12345")
                .stringType("name", "Author name")
                .stringType("bio", "His bio")
                .integerType("version", 1);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("Content-Type", "application/json");

        return builder
                .expectsToReceive("a author created event")
                .withMetadata(metadata)
                .withContent(body)
                .toPact();
    }

    /* ========= TEST ========= */
    @Test
    @PactTestFor(pactMethod = "authorCreatedPact")
    void testAuthorCreated(List<V4Interaction.AsynchronousMessage> messages) {

        String jsonReceived = messages.get(0).contentsAsString();

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");

        Message message =
                new Message(jsonReceived.getBytes(StandardCharsets.UTF_8), messageProperties);

        assertDoesNotThrow(() -> {
            listener.receiveAuthorCreatedMsg(message);
        });

        verify(authorService, times(1)).create(any(AuthorViewAMQP.class));
    }
}
