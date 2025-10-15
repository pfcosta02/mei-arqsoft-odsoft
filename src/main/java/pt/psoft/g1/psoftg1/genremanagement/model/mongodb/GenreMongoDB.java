package pt.psoft.g1.psoftg1.genremanagement.model.mongodb;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;

@Profile("mongodb")
@Primary
@Document(collection = "genres")
public class GenreMongoDB {

    @Id
    @Setter
    @Getter
    private String genreId;

    @Field("genre")
    @Size(min = 1, max = Genre.GENRE_MAX_LENGTH, message = "Genre name must be between 1 and 100 characters")
    @Getter
    private String genre;

    public GenreMongoDB(String genre){
        setGenre(genre);
    }

    protected GenreMongoDB(){
        // for ORM or deserialization only
    }

    private void setGenre(String genre) {
        if(genre == null) {
            System.out.println("Genre is null");
            throw new IllegalArgumentException("Genre cannot be null");
        }
        if(genre.isBlank()) {
            System.out.println("Genre is blank");
            throw new IllegalArgumentException("Genre cannot be blank");
        }
        int GENRE_MAX_LENGTH = 100;
        if(genre.length() > GENRE_MAX_LENGTH){
            System.out.println("Genre is too long");
            throw new IllegalArgumentException("Genre has a maximum of 100 characters");
        }
        this.genre = genre;
    }

    public String toString() { return genre; }
}