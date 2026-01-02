package pt.psoft.g1.psoftg1.bookmanagement.model;

import lombok.Getter;
import lombok.Setter;

import pt.psoft.g1.psoftg1.exceptions.ConflictException;

import pt.psoft.g1.psoftg1.shared.model.EntityWithPhoto;

import java.util.List;
import java.util.Objects;

import org.hibernate.StaleObjectStateException;

@Setter
@Getter
public class Book extends EntityWithPhoto
{
    public Long pk;
    private Long version;
    private Isbn isbn;

    public void setIsbn(String isbn) {
        this.isbn = new Isbn(isbn);
    }

    public Book(String isbn) {
        setIsbn(isbn);
    }

    public Book() {
        // got ORM only
    }

}
