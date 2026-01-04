package pt.psoft.g1.psoftg1.CDC.producer;

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
import pt.psoft.g1.psoftg1.lendingmanagement.infrastructure.impl.LendingEventsRabbitmqPublisherImpl;
import pt.psoft.g1.psoftg1.shared.dtos.LendingEventAMQP;
import pt.psoft.g1.psoftg1.configs.TestConfig;

import java.time.LocalDate;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;

import com.fasterxml.jackson.core.JsonProcessingException;

@Import(TestConfig.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {LendingEventsRabbitmqPublisherImpl.class},
        properties = {
                "stubrunner.amqp.mockConnection=true"
        }
)
@Provider("lendings_event-producer")
@PactBroker(
        url = "${pact.broker.url:http://localhost:9292}",
        authentication = @PactBrokerAuth(
                username = "${pact.broker.username:admin}",
                password = "${pact.broker.password:admin}"
        )
)
public class LendingProducerCDCIT {
    private static final Logger LOGGER = LoggerFactory.getLogger(LendingProducerCDCIT.class);


    @MockBean(name = "lendingsExchange")
    DirectExchange lendingsExchange; // corresponde ao @Qualifier("lendingsExchange")

    @MockBean
    RabbitTemplate rabbitTemplate;


    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void testTemplate(Pact pact, Interaction interaction, PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void before(PactVerificationContext context) {
        context.setTarget(new MessageTestTarget());
    }

    @PactVerifyProvider("a lending returned with commentary and rating event")
    public MessageAndMetadata lendingReturnedWithCommentary() throws JsonProcessingException {
        LOGGER.info("Generating: lending returned with commentary and rating");

        LendingEventAMQP event = LendingEventAMQP.builder()
                .lendingNumber("2025/1")
                .bookIsbn("978-1234567890")
                .readerNumber("R001")
                .startDate(LocalDate.of(2025, 1, 1))
                .returnedDate(LocalDate.of(2025, 1, 15))
                .commentary("Excelente livro, muito recomendado!")
                .rating(9)
                .version(6L)
                .build();

        Message<String> message = new LendingMessageBuilder().withLending(event).build();
        System.out.println("[PACT PROVIDER] Payload => " + message.getPayload());

        return generateMessageAndMetadata(message);
    }

    @PactVerifyProvider("a lending returned without commentary event")
    public MessageAndMetadata lendingReturnedWithoutCommentary() throws JsonProcessingException {
        LOGGER.info("Generating: lending returned without commentary");

        LendingEventAMQP event = LendingEventAMQP.builder()
                .lendingNumber("2025/2")
                .bookIsbn("978-0987654321")
                .readerNumber("R002")
                .startDate(LocalDate.of(2025, 1, 5))
                .returnedDate(LocalDate.of(2025, 1, 20))
                .commentary(null)
                .rating(7)
                .version(3L)
                .build();


        Message<String> message = new LendingMessageBuilder().withLending(event).build();
        System.out.println("[PACT PROVIDER] Payload => " + message.getPayload());

        return generateMessageAndMetadata(message);
    }

    @PactVerifyProvider("a lending created event")
    public MessageAndMetadata lendingCreated() throws JsonProcessingException {
        LOGGER.info("Generating: lending created");

        LendingEventAMQP event = LendingEventAMQP.builder()
                .lendingNumber("2025/3")
                .bookIsbn("978-1111111111")
                .readerNumber("R003")
                .startDate(LocalDate.of(2025, 1, 10))
                .returnedDate(null)
                .commentary(null)
                .rating(null)
                .version(1L)
                .build();

        Message<String> message = new LendingMessageBuilder().withLending(event).build();
        System.out.println("[PACT PROVIDER] Payload => " + message.getPayload());

        return generateMessageAndMetadata(message);
    }

    private MessageAndMetadata generateMessageAndMetadata(Message<String> message) {
        HashMap<String, Object> metadata = new HashMap<>();
        message.getHeaders().forEach((k, v) -> metadata.put(k, v));
        return new MessageAndMetadata(message.getPayload().getBytes(), metadata);
    }
}