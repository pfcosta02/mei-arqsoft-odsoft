package pt.psoft.g1.psoftg1.genremanagement.model.relational;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;

@Profile("jpa")
@Primary
@Entity
@Table(name = "Genre_Temp")
public class GenreTempEntity
{
    @Id
    //@GeneratedValue(strategy = GenerationType.AUTO)
    private String pk;

    @Size(min = 1, max = Genre.GENRE_MAX_LENGTH, message = "Genre name must be between 1 and 100 characters")
    @Column(unique = true, nullable = false, length = Genre.GENRE_MAX_LENGTH)
    @Getter
    private String genre;

    protected GenreTempEntity() { }

    public GenreTempEntity(String genre) { setGenre(genre); }

    // Getter
    public String getPk() {
        return pk;
    }

    // Setter
    private void setGenre(String genre) {
        this.genre = genre;
    }
    public void setPk(String pk) {
        this.pk = pk;
    }
}
