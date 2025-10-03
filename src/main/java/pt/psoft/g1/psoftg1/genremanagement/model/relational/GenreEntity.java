package pt.psoft.g1.psoftg1.genremanagement.model.relational;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;

@Profile("jpa")
@Primary
@Entity
@Table(name = "Genre")
public class GenreEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long pk;

    @Size(min = 1, max = Genre.GENRE_MAX_LENGTH, message = "Genre name must be between 1 and 100 characters")
    @Column(unique = true, nullable = false, length = Genre.GENRE_MAX_LENGTH)
    @Getter
    private String genre;

    protected GenreEntity() { }

    public GenreEntity(String genre) { setGenre(genre); }

    // Getter
    public Long getPk() {
        return pk;
    }

    // Setter
    private void setGenre(String genre) {
        this.genre = genre; // sem lógica
    }
}

