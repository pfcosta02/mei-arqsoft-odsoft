package CDC.consumer;

import au.com.dius.pact.core.model.*;
import au.com.dius.pact.core.model.messaging.Message;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import pt.psoft.g1.psoftg1.bookmanagement.api.BookRabbitmqController;
import pt.psoft.g1.psoftg1.bookmanagement.services.BookService;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewAMQP;
import configs.TestConfig;

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
        classes = {BookRabbitmqController.class, BookService.class}
)
public class BookCDCConsumerIT {

    @MockBean
    BookService bookService;

    @Autowired
    BookRabbitmqController listener;

    @Test
    void testBookCreatedMessageProcessing() throws Exception {

        File pactFile =
                new File("target/pacts/book_created-consumer-book_event-producer.json");

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
                listener.receiveBookCreatedMsg(rabbitMessage);
            });

            verify(bookService, times(1)).create(any(BookViewAMQP.class));
        }
    }
}
