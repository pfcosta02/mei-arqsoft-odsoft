package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;

import org.springframework.data.annotation.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Profile("mongodb")
@EqualsAndHashCode
public class IsbnMongoDB implements Serializable {

    @Id
    private String isbnId;

    @NotNull
    @Size(min = 10, max = 13)
    @Field("isbn")
    private String isbn;

    public IsbnMongoDB(String isbn) {
        setIsbn(isbn);
    }

    protected IsbnMongoDB() {};

    // Getters
    public String getIsbn() { return isbn; }

    // Setters
    private void setIsbn(String isbn) { this.isbn = isbn; }
}