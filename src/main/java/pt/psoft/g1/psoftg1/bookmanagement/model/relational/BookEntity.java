package pt.psoft.g1.psoftg1.bookmanagement.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.EntityWithPhotoEntity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("jpa")
@Primary
@Entity
@Table(name = "Book", uniqueConstraints = {
        @UniqueConstraint(name = "uc_book_isbn", columnNames = {"ISBN"})
})
public class BookEntity extends EntityWithPhotoEntity
{
    @Id
    private String bookId;

    @Version
    @Getter
    private Long version;

    @Embedded
    private IsbnEntity isbn;

    @Getter
    @Embedded
    @NotNull
    private TitleEntity title;

    @Embedded
    private DescriptionEntity description;

    @Getter
    @ManyToOne
    @NotNull
    private GenreEntity genre;

    @Getter
    @ManyToMany
    @JoinTable(joinColumns = @JoinColumn(name = "BOOK_PK"))
    private List<AuthorEntity> authors = new ArrayList<>();

    public BookEntity(IsbnEntity isbn, TitleEntity title, DescriptionEntity description, GenreEntity genre, List<AuthorEntity> authors, String photoURI)
    {
        setTitle(title);
        setIsbn(isbn);
        setDescription(description);
        setAuthors(authors);
        setGenre(genre);
        setPhotoInternal(photoURI);

        this.version = 0L;
    }

    protected BookEntity() {}

    // Setters
    public void setTitle(TitleEntity title) { this.title = title; }
    public void setIsbn(IsbnEntity isbn) { this.isbn = isbn; }
    public void setDescription(DescriptionEntity description) { this.description = description; }
    public void setGenre(GenreEntity genre) { this.genre = genre; }
    public void setAuthors(List<AuthorEntity> authors) { this.authors = authors; }
    public void setBookId(String bookId) { this.bookId = bookId; }

    // Getters
    public String getDescription(){ return description.getDescription(); }
    public String getIsbn(){ return isbn.getIsbn(); }
    public String getBookId() { return bookId; }
}
