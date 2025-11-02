package pt.psoft.g1.psoftg1.genremanagement.model.elasticsearch;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import jakarta.validation.constraints.Size;

@Document(indexName = "genres")
@NoArgsConstructor
@Getter
public class GenreES {

    @Id
    private String id;

    @Size(min = 1, max = 100, message = "Genre name must be between 1 and 100 characters")
    private String genre;

    public GenreES(String genre) {
        setGenreValue(genre);
    }

    public void setGenre(String genre) {
        setGenreValue(genre);
    }

    private void setGenreValue(String genre) {
        if (genre == null)
            throw new IllegalArgumentException("Genre cannot be null");
        if (genre.isBlank())
            throw new IllegalArgumentException("Genre cannot be blank");
        if (genre.length() > 100)
            throw new IllegalArgumentException("Genre has a maximum of 100 characters");
        this.genre = genre;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return genre;
    }
}