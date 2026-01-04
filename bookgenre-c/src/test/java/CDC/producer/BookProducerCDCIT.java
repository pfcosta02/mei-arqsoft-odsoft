package CDC.producer;

import au.com.dius.pact.core.model.Interaction;
import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactBrokerAuth;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;

import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewAMQP;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewAMQPMapper;
import pt.psoft.g1.psoftg1.bookmanagement.publishers.BookEventsPublisher;
import pt.psoft.g1.psoftg1.bookmanagement.infrastructure.publishers.impl.BookEventsRabbitmqPublisherImpl;
import configs.TestConfig;

import java.util.HashMap;
import java.util.List;

@Import(TestConfig.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = { BookEventsRabbitmqPublisherImpl.class },
        properties = {
                "stubrunner.amqp.mockConnection=true"
        }
)
@Provider("book_event-producer")
@PactBroker(
        url = "${pact.broker.url:http://localhost:9292}",
        authentication = @PactBrokerAuth(
                username = "${pact.broker.username:admin}",
                password = "${pact.broker.password:admin}"
        )
)
public class BookProducerCDCIT {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(BookProducerCDCIT.class);

    @Autowired
    BookEventsPublisher bookEventsPublisher;

    @MockBean
    RabbitTemplate template;

    @MockBean
    FanoutExchange fanout;

    @MockBean
    BookViewAMQPMapper bookViewAMQPMapper;

    // ---------- Pact bootstrap ----------

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void testTemplate(Pact pact,
                      Interaction interaction,
                      PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new MessageTestTarget());
    }

    // ---------- Provider verification ----------

    @PactVerifyProvider("a book created event")
    public MessageAndMetadata bookCreated()
            throws JsonProcessingException {

        // 1️⃣ Criar o BookViewAMQP como o producer REAL envia
        BookViewAMQP book = new BookViewAMQP(
                "9780132350884",
                "Clean Code",
                "A Handbook of Agile Software Craftsmanship",
                List.of("AUTH-1", "AUTH-2"),
                "Software Engineering"
        );
        book.setVersion(1L);

        // 2️⃣ Payload JSON
        BookMessageBuilder builder =
                new BookMessageBuilder().withBook(book);

        String payload = builder.getPayloadFromBook(book);

        // 3️⃣ Chamar o publisher REAL
        bookEventsPublisher.sendBookCreated(payload);

        // 4️⃣ Mensagem usada pelo Pact
        Message<String> message =
                new BookMessageBuilder()
                        .withBook(book)
                        .build();

        return generateMessageAndMetadata(message);
    }

    // ---------- Helpers ----------

    private MessageAndMetadata generateMessageAndMetadata(
            Message<String> message) {

        HashMap<String, Object> metadata = new HashMap<>();
        message.getHeaders()
                .forEach((k, v) -> metadata.put(k, v));

        return new MessageAndMetadata(
                message.getPayload().getBytes(),
                metadata
        );
    }
}
