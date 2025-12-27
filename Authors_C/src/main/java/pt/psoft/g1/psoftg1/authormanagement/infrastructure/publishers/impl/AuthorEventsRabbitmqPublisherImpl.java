package pt.psoft.g1.psoftg1.authormanagement.infrastructure.publishers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQPMapper;
import pt.psoft.g1.psoftg1.authormanagement.publishers.AuthorEventsPublisher;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQP;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.shared.model.AuthorEvents;
import pt.psoft.g1.psoftg1.shared.model.BookEvents;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthorEventsRabbitmqPublisherImpl implements AuthorEventsPublisher {

    private final RabbitTemplate template;

    private final FanoutExchange authorCreatedExchange;
    private final FanoutExchange authorUpdatedExchange;
    private final FanoutExchange authorDeletedExchange;

    private final AuthorViewAMQPMapper authorViewAMQPMapper;

    private int count = 0;

    @Override
    public AuthorViewAMQP sendAuthorCreated(Author author) {
        return sendAuthorEvent(author, 1L, authorCreatedExchange);
    }

    @Override
    public AuthorViewAMQP sendAuthorUpdated(Author author, Long currentVersion) {
        return sendAuthorEvent(author, currentVersion, authorUpdatedExchange);
    }

    @Override
    public AuthorViewAMQP sendAuthorDeleted(Author author, Long currentVersion) {
        return sendAuthorEvent(author, currentVersion, authorDeletedExchange);
    }

    @Override
    public void sendAuthorTempCreated(String payload) {

        try
        {
            System.out.println(" [x] Publish Author Temp CREATED into AMQP.");
            MessageProperties props = new MessageProperties();
            props.setContentType("application/json");
            Message message = new Message(payload.getBytes(StandardCharsets.UTF_8), props);
            template.send("Author.Events", BookEvents.TEMP_BOOK_CREATED, message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private AuthorViewAMQP sendAuthorEvent(Author author, Long currentVersion, FanoutExchange exchange) {

        System.out.println("Send Author event to AMQP Broker: " + author.getName());

        try {
            AuthorViewAMQP authorViewAMQP = authorViewAMQPMapper.toAuthorViewAMQP(author);
            authorViewAMQP.setVersion(currentVersion);

            ObjectMapper objectMapper = new ObjectMapper();
            String authorViewAMQPinString = objectMapper.writeValueAsString(authorViewAMQP);

            this.template.convertAndSend(exchange.getName(), "", authorViewAMQPinString);

            return authorViewAMQP;
        }
        catch( Exception ex ) {
            System.out.println(" [x] Exception sending author event: '" + ex.getMessage() + "'");

            return null;
        }
    }
}