package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;
import pt.psoft.g1.psoftg1.shared.model.mongodb.EntityWithPhotoMongoDB;

import java.util.ArrayList;
import java.util.List;

@Profile("mongodb")
@Primary
@Document(collection = "books")
public class BookMongoDB extends EntityWithPhotoMongoDB {

    @Id
    private String bookId;

    @Getter
    @Version
    @Field("version")
    private Long version;

    @Field("isbn")
    private IsbnMongoDB isbn;

    @Getter
    @NotNull
    @Field("title")
    private TitleMongoDB title;

    @Getter
    @NotNull
    @Field("genre")
    private GenreMongoDB genre;

    @Getter
    @Field("authors")
    private List<AuthorMongoDB> authors = new ArrayList<>();

    @Field("description")
    private DescriptionMongoDB description;

    public BookMongoDB(IsbnMongoDB isbn, TitleMongoDB title, DescriptionMongoDB description, GenreMongoDB genre, List<AuthorMongoDB> authors, String photo)
    {
        setTitle(title);
        setIsbn(isbn);
        setDescription(description);
        setAuthors(authors);
        setGenre(genre);
        setPhotoInternal(photo);
    }

    protected BookMongoDB() {}

    // Setters
    public void setTitle(TitleMongoDB title) { this.title = title; }
    public void setIsbn(IsbnMongoDB isbn) { this.isbn = isbn; }
    public void setDescription(DescriptionMongoDB description) { this.description = description; }
    public void setGenre(GenreMongoDB genre) { this.genre = genre; }
    public void setAuthors(List<AuthorMongoDB> authors) { this.authors = authors; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    // Getters
    public String getDescription(){ return description.getDescription(); }
    public String getIsbn(){ return isbn.getIsbn(); }
    public String getBookId(){ return bookId; }
}