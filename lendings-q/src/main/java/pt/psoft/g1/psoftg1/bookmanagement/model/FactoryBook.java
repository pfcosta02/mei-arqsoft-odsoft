package pt.psoft.g1.psoftg1.bookmanagement.model;

import org.springframework.stereotype.Component;

@Component
public class FactoryBook {

    public Book newBook(String isbn) {
        return new Book(isbn);
    }
}
