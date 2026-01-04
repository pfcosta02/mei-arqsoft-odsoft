package pt.psoft.g1.psoftg1.bookmanagement.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.authormanagement.model.relational.AuthorEntity;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreEntity;
import pt.psoft.g1.psoftg1.genremanagement.model.relational.GenreTempEntity;
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

    @Getter
    @Embedded
    private IsbnEntity isbn;

    @Getter
    @Embedded
    @NotNull
    private TitleEntity title;

    @Getter
    @Embedded
    private DescriptionEntity description;

    @Getter
    @NotNull
    private String genre;

    @Getter
    @ElementCollection
    @CollectionTable(
            name = "book_temp_authors",
            joinColumns = @JoinColumn(name = "book_pk")
    )
    @Column(name = "author_number", nullable = false)
    private List<String> authorNumbers = new ArrayList<>();

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    protected BookTempEntity() {}

    public BookTempEntity(
            IsbnEntity isbn,
            TitleEntity title,
            DescriptionEntity description,
            String genre,
            List<String> authorNumbers
    ) {
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.authorNumbers = authorNumbers;
    }

    public boolean isReadyToFinalize() {
        return genre != null
                && authorNumbers != null
                && !authorNumbers.isEmpty();
    }
}