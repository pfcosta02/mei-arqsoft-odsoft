package pt.psoft.g1.psoftg1.bookmanagement.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.shared.model.relational.EntityWithPhotoEntity;

import java.util.ArrayList;
import java.util.List;

@Profile("jpa")
@Primary
@Entity
@Table(name = "book_temp", uniqueConstraints = {
        @UniqueConstraint(name = "uc_book_temp_isbn", columnNames = {"isbn"})
})
public class BookTempEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Version
    private Long version;

    // Dados iniciais (request)
    @NotNull
    private String isbn;

    @NotNull
    private String title;

    private String description;

    @NotNull
    private String authorName;

    @NotNull
    private String authorBio;

    @NotNull
    private String genreName;

    // Resultados da saga (preenchidos por eventos)
    private Long authorId;
    private Long genreId;

    private boolean authorCreated;
    private boolean genreCreated;

    protected BookTempEntity() {}

    public BookTempEntity(
            String isbn,
            String title,
            String description,
            String authorName,
            String authorBio,
            String genreName
    ) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.authorName = authorName;
        this.authorBio = authorBio;
        this.genreName = genreName;
        this.authorCreated = false;
        this.genreCreated = false;
    }
}