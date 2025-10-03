package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoDB;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;
import pt.psoft.g1.psoftg1.shared.model.mongodb.EntityWithPhotoMongoDB;

import java.util.List;

@Document(collection = "books")
@EnableMongoAuditing
public class BookMongoDB extends EntityWithPhotoMongoDB {

    @Getter
    @Setter
    @Id
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

    private void setTitle(String title) {
        this.title = new TitleMongoDB(title);
    }

    private void setIsbn(String isbn) {
        this.isbn = new IsbnMongoDB(isbn);
    }

    private void setDescription(String description) {
        this.description = new DescriptionMongoDB(description);
    }

    public void setGenre(GenreMongoDB genre) {
        this.genre = genre;
    }

    public void setAuthors(List<AuthorMongoDB> authors) {
        this.authors = authors;
    }

    public String getDescription() {
        return this.description.toString();
    }

    public BookMongoDB(String isbn, String title, String description, GenreMongoDB genre, List<AuthorMongoDB> authors, String photoURI) {
        setTitle(title);
        setIsbn(isbn);
        if(description != null)
            setDescription(description);
        if(genre == null)
            throw new IllegalArgumentException("Genre cannot be null");
        if(authors == null)
            throw new IllegalArgumentException("Authors cannot be null");
        if(authors.isEmpty())
            throw new IllegalArgumentException("Authors cannot be empty");

        setAuthors(authors);
        setGenre(genre);
        setPhotoInternal(photoURI);
    }

    protected BookMongoDB() {
        // got ORM only
    }

    public void removePhoto(long desiredVersion) {
        if(desiredVersion != this.version) {
            throw new ConflictException("Provided version does not match latest version of this object");
        }

        setPhotoInternal(null);
    }

    public String getIsbn(){
        return this.isbn.toString();
    }

}

