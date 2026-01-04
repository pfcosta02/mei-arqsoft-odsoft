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
import configs.TestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.Message;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQP;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQPMapper;
import pt.psoft.g1.psoftg1.authormanagement.infrastructure.publishers.impl.AuthorEventsRabbitmqPublisherImpl;
import pt.psoft.g1.psoftg1.authormanagement.publishers.AuthorEventsPublisher;

import java.util.HashMap;
import java.util.List;

@Import(TestConfig.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = { AuthorEventsRabbitmqPublisherImpl.class },
        properties = {
                "stubrunner.amqp.mockConnection=true"
        }
)
@Provider("author_event-producer")
@PactBroker(
        url = "${pact.broker.url:http://localhost:9292}",
        authentication = @PactBrokerAuth(
                username = "${pact.broker.username:admin}",
                password = "${pact.broker.password:admin}"
        )
)
public class AuthorProducerCDCIT {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AuthorProducerCDCIT.class);

    @Autowired
    AuthorEventsPublisher authorEventsPublisher;

    @MockBean
    RabbitTemplate template;

    @MockBean
    FanoutExchange fanout;

    @MockBean
    AuthorViewAMQPMapper authorViewAMQPMapper;

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

    @PactVerifyProvider("a author created event")
    public MessageAndMetadata authorCreated()
            throws JsonProcessingException {

        // 1️⃣ Criar o AuthorViewAMQP como o producer REAL envia
        AuthorViewAMQP author = new AuthorViewAMQP(
                "A name",
                "His bio"
        );
        author.setVersion(1L);
        author.setAuthorNumber("12345");

        // 2️⃣ Payload JSON
        AuthorMessageBuilder builder =
                new AuthorMessageBuilder().withAuthor(author);

        String payload = builder.getPayloadFromAuthor(author);

        // 3️⃣ Chamar o publisher REAL
        authorEventsPublisher.sendAuthorCreated(payload);

        // 4️⃣ Mensagem usada pelo Pact
        Message<String> message =
                new AuthorMessageBuilder()
                        .withAuthor(author)
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
