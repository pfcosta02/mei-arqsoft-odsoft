package pt.psoft.g1.psoftg1.bookmanagement.model.mongodb;



import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Profile("mongodb")
@Document(collection = "books")
@Primary
@EqualsAndHashCode
public class IsbnMongoDB implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long IsbnId;

    @NotNull
    @Size(min = 10, max = 13)
    @Column(name = "ISBN", length = 16, unique = true, nullable = false)
    private String isbn;

    public IsbnMongoDB(String isbn) {
        setIsbn(isbn);
    }

    protected IsbnMongoDB() {};

    // Getters
    public String getIsbn()
    {
        return isbn;
    }

    // Setters
    private void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }
}

