package pt.psoft.g1.psoftg1.bookmanagement.services;


import pt.psoft.g1.psoftg1.bookmanagement.api.BookViewAMQP;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.model.DTOs.AuthorTempCreatedDTO;
import pt.psoft.g1.psoftg1.isbn.model.BookInfo;
import pt.psoft.g1.psoftg1.shared.services.Page;

import java.util.List;

/**
 *
 */
public interface BookService {
    Book create(CreateBookRequest request, String isbn);
    BookAuthorGenreDTO createSAGA(CreateBookAuthorGenreRequest request, String isbn); // SAGA
    Book create(BookViewAMQP bookViewAMQP); //AMQP request
    Book save(Book book);
    Book findByIsbn(String isbn);
    Book update(UpdateBookRequest request, String currentVersion);
    void updateTemp(AuthorTempCreatedDTO authorTempCreatedDTO);
    Book removeBookPhoto(String isbn, long desiredVersion);

}
