package CDC.consumer;

import au.com.dius.pact.core.model.DefaultPactReader;
import au.com.dius.pact.core.model.Pact;
import au.com.dius.pact.core.model.PactReader;
import au.com.dius.pact.core.model.messaging.Message;
import configs.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorRabbitmqController;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQP;
import pt.psoft.g1.psoftg1.authormanagement.services.AuthorService;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(TestConfig.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = {AuthorRabbitmqController.class, AuthorService.class}
)
public class AuthorCDCConsumerIT {

    @MockBean
    AuthorService authorService;

    @Autowired
    AuthorRabbitmqController listener;

    @Test
    void testAuthorCreatedMessageProcessing() throws Exception {

        File pactFile =
                new File("target/pacts/author_created-consumer-author_event-producer.json");

        PactReader pactReader = DefaultPactReader.INSTANCE;
        Pact pact = pactReader.loadPact(pactFile);

        List<Message> messages = pact.asMessagePact().get().getMessages();

        for (Message messageFromPact : messages) {

            String jsonReceived = messageFromPact.contentsAsString();

            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("application/json");

            org.springframework.amqp.core.Message rabbitMessage =
                    new org.springframework.amqp.core.Message(
                            jsonReceived.getBytes(StandardCharsets.UTF_8),
                            messageProperties
                    );

            assertDoesNotThrow(() -> {
                listener.receiveAuthorCreatedMsg(rabbitMessage);
            });

            verify(authorService, times(1)).create(any(AuthorViewAMQP.class));
        }
    }
}
