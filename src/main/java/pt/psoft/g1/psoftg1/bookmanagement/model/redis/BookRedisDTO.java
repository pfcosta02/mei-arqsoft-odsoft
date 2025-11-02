package pt.psoft.g1.psoftg1.bookmanagement.model.redis;

import pt.psoft.g1.psoftg1.authormanagement.model.redis.AuthorRedisDTO;
import pt.psoft.g1.psoftg1.genremanagement.model.redis.GenreRedisDTO;

import java.io.Serializable;
import java.util.List;

public class BookRedisDTO implements Serializable {
    private String bookId;
    private Long version;
    private String isbn;
    private String title;
    private String description;
    private GenreRedisDTO genre;
    private List<AuthorRedisDTO> authors;
    private String photoURI;

    public BookRedisDTO() {}

    public BookRedisDTO(String bookId, Long version, String isbn, String title, String description, GenreRedisDTO genre, List<AuthorRedisDTO> authors, String photoURI) {
        this.bookId = bookId;
        this.version = version;
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.authors = authors;
        this.photoURI = photoURI;
    }


    // Getters e setters
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GenreRedisDTO getGenre() {
        return genre;
    }

    public void setGenre(GenreRedisDTO genre) {
        this.genre = genre;
    }

    public List<AuthorRedisDTO> getAuthors() {
        return authors;
    }

    public void setAuthors(List<AuthorRedisDTO> authors) {
        this.authors = authors;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }
}
