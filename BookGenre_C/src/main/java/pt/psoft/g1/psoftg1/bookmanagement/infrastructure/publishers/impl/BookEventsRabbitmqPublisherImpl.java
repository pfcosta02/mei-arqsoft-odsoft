package pt.psoft.g1.psoftg1.bookmanagement.infrastructure.publishers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewAMQP;
import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewAMQPMapper;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.publishers.BookEventsPublisher;
import pt.psoft.g1.psoftg1.shared.model.BookEvents;

@Service
@RequiredArgsConstructor
public class BookEventsRabbitmqPublisherImpl implements BookEventsPublisher {

    private final RabbitTemplate template;

    private final FanoutExchange bookCreatedExchange;
    private final FanoutExchange bookUpdatedExchange;
    private final FanoutExchange bookDeletedExchange;

    private final BookViewAMQPMapper bookViewAMQPMapper;

    private int count = 0;

    @Override
    public BookViewAMQP sendBookCreated(Book book) {
        return sendBookEvent(book, 1L, bookCreatedExchange);
    }

    @Override
    public BookViewAMQP sendBookUpdated(Book book, Long currentVersion) {
        return sendBookEvent(book, currentVersion, bookUpdatedExchange);
    }

    @Override
    public BookViewAMQP sendBookDeleted(Book book, Long currentVersion) {
        return sendBookEvent(book, currentVersion, bookDeletedExchange);
    }

    private BookViewAMQP sendBookEvent(Book book, Long currentVersion, FanoutExchange exchange) {

        System.out.println("Send Book event to AMQP Broker: " + book.getTitle());

        try {
            BookViewAMQP bookViewAMQP = bookViewAMQPMapper.toBookViewAMQP(book);
            bookViewAMQP.setVersion(currentVersion);

            ObjectMapper objectMapper = new ObjectMapper();
            String bookViewAMQPinString = objectMapper.writeValueAsString(bookViewAMQP);

            this.template.convertAndSend(exchange.getName(), "", bookViewAMQPinString);

            return bookViewAMQP;
        }
        catch( Exception ex ) {
            System.out.println(" [x] Exception sending book event: '" + ex.getMessage() + "'");

            return null;
        }
    }
}