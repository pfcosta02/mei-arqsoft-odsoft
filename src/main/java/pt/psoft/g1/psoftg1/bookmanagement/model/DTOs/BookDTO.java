package pt.psoft.g1.psoftg1.bookmanagement.model.DTOs;

import pt.psoft.g1.psoftg1.shared.model.Name;

import java.io.Serializable;
import java.util.List;

public class BookDTO implements Serializable {
    private String bookId;
    private Long version;
    private String isbn;
    private String title;
    private String description;
    private String genreName;
    private List<String> authors;
    private String photoURI;

    public BookDTO() {}

    public BookDTO(String bookId,  Long version, String isbn, String title, String description, String genreName, List<String> authors, String photoURI) {
        this.bookId = bookId;
        this.version = version;
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.genreName = genreName;
        this.authors = authors;
        this.photoURI = photoURI;
    }

    public String getBookId() { return bookId; }
    public Long getVersion() { return version; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getgenreName() { return genreName; }
    public List<String> getAuthors() { return authors; }
    public String getPhotoURI() { return photoURI; }
}
