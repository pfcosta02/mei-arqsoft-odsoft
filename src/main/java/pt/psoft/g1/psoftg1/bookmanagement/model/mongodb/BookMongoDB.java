package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

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

import java.util.List;

@Profile("mongodb")
@Primary
@Document(collection = "books")
public class BookMongoDB extends EntityWithPhotoMongoDB {

    @Id
    @Getter
    @Setter
    private String bookId;

    @Getter
    @Setter
    @Version
    @Field("version")
    private Long version;

    @Getter
    @Field("isbn")
    private IsbnMongoDB isbn;

    @Getter
    @Field("title")
    private TitleMongoDB title;

    @Getter
    @Field("genre")
    private GenreMongoDB genre;

    @Getter
    @Field("authors")
    private List<AuthorMongoDB> authors;

    @Field("description")
    DescriptionMongoDB description;

    public BookMongoDB(IsbnMongoDB isbn, TitleMongoDB title, DescriptionMongoDB description, GenreMongoDB genre, List<AuthorMongoDB> authors, String photoURI)
    {
        setTitle(title);
        setIsbn(isbn);
        setDescription(description);
        setAuthors(authors);
        setGenre(genre);
        setPhotoInternal(photoURI);
    }

    protected BookMongoDB() {}

    // Setters
    private void setTitle(TitleMongoDB title) { this.title = title; }
    private void setIsbn(IsbnMongoDB isbn) { this.isbn = isbn; }
    private void setDescription(DescriptionMongoDB description) { this.description = description; }
    public void setGenre(GenreMongoDB genre) { this.genre = genre; }
    public void setAuthors(List<AuthorMongoDB> authors) { this.authors = authors; }

    // Getters
    public String getDescription(){ return this.description.toString(); }
    public String getIsbn(){ return this.isbn.toString(); }
}