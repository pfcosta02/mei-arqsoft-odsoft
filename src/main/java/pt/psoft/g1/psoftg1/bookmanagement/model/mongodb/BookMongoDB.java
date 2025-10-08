package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.authormanagement.model.mongodb.AuthorMongoDB;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.DescriptionEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.IsbnEntity;
import pt.psoft.g1.psoftg1.bookmanagement.model.relational.TitleEntity;
import pt.psoft.g1.psoftg1.exceptions.ConflictException;
import pt.psoft.g1.psoftg1.genremanagement.model.mongodb.GenreMongoDB;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.shared.model.mongodb.EntityWithPhotoMongoDB;

import java.util.List;

@Profile("mongodb")
@Primary
@Document(collection = "books")
@EnableMongoAuditing
public class BookMongoDB extends EntityWithPhotoMongoDB {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long pk;

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
    private void setGenre(GenreMongoDB genre) { this.genre = genre; }
    private void setAuthors(List<AuthorMongoDB> authors) { this.authors = authors; }

    // Getters
    public String getDescription(){ return this.description.toString(); }
    public String getIsbn(){ return this.isbn.toString(); }
}

