package pt.psoft.g1.psoftg1.isbn.model;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Setter
@Getter
public class BookInfo {
    private String title;
    private String author;
    private String isbn;
    private String publisher;

    public BookInfo(String title, String author, String isbn, String publisher) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
    }

    public BookInfo() {}
}



