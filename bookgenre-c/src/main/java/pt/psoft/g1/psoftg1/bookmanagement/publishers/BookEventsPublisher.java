package pt.psoft.g1.psoftg1.bookmanagement.publishers;

import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewAMQP;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;

public interface BookEventsPublisher {

    BookViewAMQP sendBookCreated(Book book);

    void sendBookTempCreated(String payload); // temporario

    void sendBookFinalized(String payload);

    void sendBookCreated(String payload);

    BookViewAMQP sendBookUpdated(Book book, Long currentVersion);

    BookViewAMQP sendBookDeleted(Book book, Long currentVersion);
}
