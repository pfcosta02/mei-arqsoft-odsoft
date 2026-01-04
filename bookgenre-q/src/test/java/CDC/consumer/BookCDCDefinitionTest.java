package CDC.consumer;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.*;
import au.com.dius.pact.core.model.*;
import au.com.dius.pact.core.model.annotations.Pact;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import pt.psoft.g1.psoftg1.bookmanagement.api.BookRabbitmqController;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookService;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewAMQP;
import configs.TestConfig;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(TestConfig.class)
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {BookRabbitmqController.class, BookService.class}
)
@PactConsumerTest
@PactTestFor(
        providerName = "book_event-producer",
        providerType = ProviderType.ASYNCH,
        pactVersion = PactSpecVersion.V4
)
public class BookCDCDefinitionTest {

    @MockBean
    BookService bookService;

    @Autowired
    BookRabbitmqController listener;

    /* ========= CONTRACT ========= */
    @Pact(consumer = "book_created-consumer")
    V4Pact bookCreatedPact(MessagePactBuilder builder) {

        PactDslJsonBody body = new PactDslJsonBody()
                .stringType("isbn", "9780132350884")
                .stringType("title", "Clean Code")
                .stringType("description", "A Handbook of Agile Software Craftsmanship")
                .stringType("genre", "Software Engineering")
                .integerType("version", 1);

        body.array("authorIds")
                .stringType("AUTH-001")
                .stringType("AUTH-002")
                .closeArray();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("Content-Type", "application/json");

        return builder
                .expectsToReceive("a book created event")
                .withMetadata(metadata)
                .withContent(body)
                .toPact();
    }

    /* ========= TEST ========= */
    @Test
    @PactTestFor(pactMethod = "bookCreatedPact")
    void testBookCreated(List<V4Interaction.AsynchronousMessage> messages) {

        String jsonReceived = messages.get(0).contentsAsString();

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");

        Message message =
                new Message(jsonReceived.getBytes(StandardCharsets.UTF_8), messageProperties);

        assertDoesNotThrow(() -> {
            listener.receiveBookCreatedMsg(message);
        });

        verify(bookService, times(1)).create(any(BookViewAMQP.class));
    }
}
