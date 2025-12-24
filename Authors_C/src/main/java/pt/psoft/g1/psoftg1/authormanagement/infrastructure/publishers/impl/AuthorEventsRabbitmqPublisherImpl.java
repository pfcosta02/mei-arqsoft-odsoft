package pt.psoft.g1.psoftg1.authormanagement.infrastructure.publishers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQPMapper;
import pt.psoft.g1.psoftg1.authormanagement.publishers.AuthorEventsPublisher;
import pt.psoft.g1.psoftg1.authormanagement.api.AuthorViewAMQP;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.shared.model.AuthorEvents;

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